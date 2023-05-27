package com.micro.otp.entity

data class GenerateOtpRequest(val phoneNumber: String , val fromLoginOrSignUp:String , val userId:String)
