package com.micro.otp.controller

import com.micro.otp.entity.GenerateOtpRequest
import com.micro.otp.service.OtpService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/otp")
class otpController {

    @Autowired
    private lateinit var otpService: OtpService

    @PostMapping("/generateOtp")
    fun generateOtp(@RequestBody request: GenerateOtpRequest): String {
       return otpService.requestOtp(request.phoneNumber , request.userEmail)
    }
}