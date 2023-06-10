package com.micro.account.entity.response

import com.micro.account.entity.ErrorCode

data class ErrorResponse(val errorCode: ErrorCode, val errorMessage: String)
