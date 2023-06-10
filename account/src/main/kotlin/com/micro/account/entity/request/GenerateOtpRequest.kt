package com.micro.account.entity.request

data class GenerateOtpRequest(val phoneNumber: String , val userEmail: String, val userId: String)
