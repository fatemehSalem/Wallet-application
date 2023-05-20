package com.micro.otp.service

import com.micro.otp.entity.OtpUserCode
import com.micro.otp.repository.OtpRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class OtpService(private val otpRepository: OtpRepository) {

    fun requestOtp(phoneNumber: String , userEmail: String): String {

        val otp = generateOtp()
        val otpUserCode = OtpUserCode()
        if(userEmail != null){
            otpUserCode.userEmail = userEmail
            otpUserCode.userPhoneNumber =""
            otpUserCode.otpCode = generateRandomNumber()

        } else if(phoneNumber!= null){
            otpUserCode.userPhoneNumber = phoneNumber
            otpUserCode.userEmail = ""
            otpUserCode.otpCode = splitLastFourDigits(phoneNumber)
        }
        otpRepository.save(otpUserCode)

        return otp
    }

    private fun splitLastFourDigits(phoneNumber: String): String {
        val digitsOnly = phoneNumber.replace("[^\\d]".toRegex(), "")
        return digitsOnly.takeLast(4)
    }

    private fun generateRandomNumber(): String {
        val r = Random()
        return (r.nextInt(100000 - 999999) + 100000).toString()
    }

    fun verifyOtpCodes(phoneNumber: String, userEmail: String , otp: String): Boolean {
        var storedOtp = OtpUserCode()
        if(userEmail != null){
             storedOtp = otpRepository.findByUserEmail(userEmail)

        } else if(phoneNumber!= null){
             storedOtp = otpRepository.findByUserPhoneNumber(phoneNumber)
        }

        return otp == storedOtp.otpCode
    }

    private fun generateOtp(): String {
        // Generate a random 6-digit OTP
        val otp = Random().nextInt(900000) + 100000
        return otp.toString()
    }

/*    private fun generateOtpAgain(): String {
       //generate OTP again
       //Update User previous OTP with new one
    }*/
}