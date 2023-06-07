package com.micro.account.utils

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

object PasswordUtils {
    private val passwordEncoder = BCryptPasswordEncoder()

    fun encryptPassword(password: String): String {
        return passwordEncoder.encode(password)
    }

    fun verifyPassword(password: String, encryptedPassword: String): Boolean {
        return passwordEncoder.matches(password, encryptedPassword)
    }


}