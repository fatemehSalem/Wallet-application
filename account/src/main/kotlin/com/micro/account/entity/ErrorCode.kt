package com.micro.account.entity

enum class ErrorCode(val code: Int, val message: String) {
    INVALID_USERNAME(1001, "Invalid username"),
    INVALID_PHONE_NUMBER(1002, "Invalid phone number"),
    // Add more error codes here as needed
}