/*
package com.micro.auth.config

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Component

@Component
class CustomAuthProvider(private val userRepository: UserRepository) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.name
        val password = authentication.credentials.toString()

        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("Invalid username or password")

        if (!BCrypt.checkpw(password, user.password)) {
            throw BadCredentialsException("Invalid username or password")
        }

        return UsernamePasswordAuthenticationToken(user, null, user.authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}
*/
