package com.micro.transaction.entity.response

data class CustomResponse<Any>(
    //val data: T?,
    val data: Any,
    val message: String,
    val errorCode: Int? = null
)
