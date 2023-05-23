package com.micro.otp.entity

enum class ErrorCode(val code: Int, val message: String) {
    INVALID_OTP_CODE(2001, "Invalid username"),
    TOKEN_GENERATION_FAILED(2002, "Token generation is failed")
    // Add more error codes here as needed
}