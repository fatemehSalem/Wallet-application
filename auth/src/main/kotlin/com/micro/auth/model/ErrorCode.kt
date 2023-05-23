package com.micro.auth.model

enum class ErrorCode(val code: Int, val message: String) {
    TOKEN_GENERATION_FAILED(3001, "Token generation is failed")
}
