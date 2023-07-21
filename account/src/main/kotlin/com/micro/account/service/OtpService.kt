package com.micro.account.service

import com.micro.account.entity.request.GenerateOtpCodeRequest
import com.micro.account.entity.response.CustomResponse
import com.micro.account.entity.response.GenerateOtpResponse
import com.micro.account.utils.GeneralUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class OtpService(
    private val redisService: RedisService
) {

    fun generateNewOtpCodeAfterExpiration(request: GenerateOtpCodeRequest): ResponseEntity<Any> {
        return try {
            val code = GeneralUtils.splitLastFourDigits(request.user_phone_number)
            val response = CustomResponse(
                GenerateOtpResponse(
                    redisService.saveOtpDataInRedis(
                        code, request.user_phone_number, System.currentTimeMillis()
                    )
                ), "Generating a new OTP code was successful!"
            )
            ResponseEntity.ok(response)
        }  catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error occurred while generating the new OTP code.")
        }

    }
}