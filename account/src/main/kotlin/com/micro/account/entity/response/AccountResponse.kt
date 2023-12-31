package com.micro.account.entity.response

import com.micro.account.entity.model.Payload

data class AccountResponse(
    val status: Int?,
    val code: String?,
    val message: String?,
    val payload: Payload?
)

