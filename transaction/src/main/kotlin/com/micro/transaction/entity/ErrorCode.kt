package com.micro.transaction.entity

enum class ErrorCode (val code: Int) {
    ACCOUNT_IS_NOT_FOUND(1006),
    PERSONAL_TO_PERSONAL_TRANSFER_WAS_UNSUCCESSFUL(1009),
    TOP_UP_TRANSACTION_WAS_UNSUCCESSFUL(1011)
}