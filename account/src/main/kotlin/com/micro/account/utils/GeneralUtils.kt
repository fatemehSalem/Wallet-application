package com.micro.account.utils

import kotlin.random.Random

object GeneralUtils {
    fun generateRandomString(): String {
        val length = Random.nextInt(8, 11)
        val chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }

    fun splitLastFourDigits(phoneNumber: String): String {
        val lastIndex = phoneNumber.length - 1
        return if (lastIndex >= 3) {
            phoneNumber.substring(lastIndex - 3)
        } else {
            phoneNumber
        }
    }
    fun generateRandomNumber(): String {
        val random = Random(System.currentTimeMillis())
        return random.nextInt(10000000, 99999999).toString()
    }
/*
    fun createCustomResponse(data: Any, message: String, status: Int): CustomResponse<Any> {
        val data = data
        val message = message
        val status = status
        return CustomResponse(data, message, status)
    }*/

 /*   fun createErrorResponse(errorCode:ErrorCode , errorMessage:String): ErrorResponse {
        val errorCode = ErrorCode.INVALID_PHONE_NUMBER
        val errorMessage = "Invalid phone number or password"
        val errorResponse = ErrorResponse(errorCode, errorMessage)

    }*/
}