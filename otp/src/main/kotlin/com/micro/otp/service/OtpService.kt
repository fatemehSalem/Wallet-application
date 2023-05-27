package com.micro.otp.service

import com.micro.otp.entity.OtpUserCode
import com.micro.otp.repository.OtpRepository
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Service
class OtpService(private val otpRepository: OtpRepository,
                 private val webClient: WebClient,) {


   /* private fun splitLastFourDigits(phoneNumber: String): String {
        val digitsOnly = phoneNumber.replace("[^\\d]".toRegex(), "")
        return digitsOnly.takeLast(4)
    }*/
    fun splitLastFourDigits(phoneNumber: String): String {
        val lastIndex = phoneNumber.length - 1
        return if (lastIndex >= 3) {
            phoneNumber.substring(lastIndex - 3)
        } else {
            phoneNumber // Return the whole string if it has less than 4 characters
        }
    }

    private fun generateRandomNumber(): String {
        val r = Random()
        return (r.nextInt(100000 - 999999) + 100000).toString()
    }

    fun verifyOtpCodes(phoneNumber: String, otp: String): OtpUserCode {
        return otpRepository.findByUserPhoneNumber(phoneNumber)
    }

    fun generateOtp(phoneNumber: String): String {
        val otpUserCode = OtpUserCode()
            otpUserCode.userPhoneNumber = phoneNumber
            otpUserCode.otpCode = splitLastFourDigits(phoneNumber)

        otpRepository.save(otpUserCode)
        return otpUserCode.otpCode
    }

    /*-----------------------------TOKEN----------------------------*/
    fun generateToken(phoneNumber: String): String? {
        return  webClient.post()
            .uri("http://api/auth/generateToken:8080")
            .bodyValue(phoneNumber)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
    }

}