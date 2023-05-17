package com.micro.account.controller

import com.micro.account.entity.LoginRequest
import com.micro.account.entity.LoginResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

class AuthController(private val authenticationManager: AuthenticationManager) {
/*
    @PostMapping("/authenticate")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<*> {
        val auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
        )

        val token = ""

        return ResponseEntity.ok(LoginResponse(token))
    }*/

  /*  @PostMapping("/authenticate")
    fun authenticate(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val token = UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
        val auth = authenticationManager.authenticate(token)
        val accessToken = auth.details as String
        return ResponseEntity.ok(LoginResponse(accessToken, loginRequest.username))
    }*/
}