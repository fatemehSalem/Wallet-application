package com.micro.auth.controller

import com.micro.auth.model.TokenGenerateRequest
import com.micro.auth.service.JwtTokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController {
    @Autowired
    private lateinit var jwtTokenService: JwtTokenService

    @PostMapping("/generateToken")
    fun generateOtp(@RequestBody request: TokenGenerateRequest): String? {
        return jwtTokenService.createAuthenticationToken(request.phoneNumber)
    }
}