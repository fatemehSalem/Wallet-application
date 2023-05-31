package com.micro.account.entity
data class AccountResponse(
    val status: Int?,
    val code: String?,
    val message: String?,
    val payload: Payload?
)

