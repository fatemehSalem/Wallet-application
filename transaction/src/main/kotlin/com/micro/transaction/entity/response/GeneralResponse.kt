package com.micro.transaction.entity.response

import com.micro.transaction.entity.model.Payload

data class GeneralResponse (
    val status: Int?,
    val code: String?,
    val message: String?,
    val payload: Payload?
)
