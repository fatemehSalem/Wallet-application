package com.micro.account.entity

enum class ErrorCode(val code: Int) {
    INVALID_USERNAME(1001),
    INVALID_PHONE_NUMBER(1002),
    CREATE_ACCOUNT_WAS_UNSUCCESSFUL(1003),
    INVALID_OTP_CODE(1004),
    ACCOUNT_EXIST_WITH_THIS_PHONE_NUMBER(1005),
    ACCOUNT_IS_NOT_FOUND(1006),
    ACCOUNT_AUTHENTICATION_WAS_UNSUCCESSFUL(1007)
}