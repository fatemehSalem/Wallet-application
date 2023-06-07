package com.micro.account.entity

data class CustomResponse<Any>(
    //val data: T?,
    val data: Any,
    val message: String,
    val errorCode: Int? = null
)
