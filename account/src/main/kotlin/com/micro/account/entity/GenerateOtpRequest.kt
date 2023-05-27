package com.micro.account.entity

data class GenerateOtpRequest(val phoneNumber: String , val userEmail: String, val userId: String)
