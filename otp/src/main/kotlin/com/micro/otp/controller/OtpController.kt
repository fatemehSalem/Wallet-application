package com.micro.otp.controller

import com.micro.otp.entity.ErrorCode
import com.micro.otp.entity.ErrorResponse
import com.micro.otp.entity.GenerateOtpRequest
import com.micro.otp.entity.VerifyOtpRequest
import com.micro.otp.service.OtpService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient

@RestController
@RequestMapping("/api/otp")
class OtpController () {
    @Autowired
    private lateinit var otpService: OtpService

    @PostMapping("/generateOtp")
    fun generateOtp(@RequestBody request: GenerateOtpRequest): String {
       return otpService.generateOtp(request.phoneNumber)
    }

    @PostMapping("/verifyOtp")
    fun verifyOtp(@RequestBody request: VerifyOtpRequest) {
        if(otpService.verifyOtpCodes(request.phoneNumber , request.otpCode)){
            //generate JWT token
            try {
                 ResponseEntity.ok().body(otpService.generateToken(request.phoneNumber))
            }catch (e: Exception){
                val errorCode = ErrorCode.TOKEN_GENERATION_FAILED
                val errorMessage = "Token generation is failed"
                val errorResponse = ErrorResponse(errorCode, errorMessage)
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
           }
        }else{
            // OTP code verification failed
            val errorCode = ErrorCode.INVALID_OTP_CODE
            val errorMessage = "Invalid OTP code"
            val errorResponse = ErrorResponse(errorCode, errorMessage)
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
        }
    }
}