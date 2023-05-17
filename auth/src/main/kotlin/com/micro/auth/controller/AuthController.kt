/*
package com.micro.auth.controller

import com.micro.auth.request.LoginRequest
import com.micro.auth.response.LoginResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authenticationManager: AuthenticationManager) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<*> {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )

        SecurityContextHolder.getContext().authentication = authentication
        val token =""

   */
/*     val token = JwtUtils.generateToken(authentication.principal as User)*//*


        return ResponseEntity.ok(LoginResponse(token))
    }
}
*/
