package com.micro.account.utils

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

object PasswordEncoderUtils {

    fun encryptPassword(password: String): String {
        val passwordEncoder = BCryptPasswordEncoder()
        return passwordEncoder.encode(password)
    }

    fun verifyPassword(rawPassword: String, encryptedPassword: String): Boolean {
        val passwordEncoder = BCryptPasswordEncoder()
        return passwordEncoder.matches(rawPassword, encryptedPassword)
    }
}