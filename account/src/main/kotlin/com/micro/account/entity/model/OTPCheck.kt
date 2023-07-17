package com.micro.account.entity.model

data class OTPCheck(
    var uuid: String,
    var user_phone_number:String ,
    var user_otp_code: String)
