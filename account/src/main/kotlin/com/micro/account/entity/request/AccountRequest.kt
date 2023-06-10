package com.micro.account.entity.request

data class AccountRequest(
    val user_first_name: String,
    val user_last_name: String,
    val user_phone_country_code: String,
    val user_phone_number: String,
    val user_email: String,
    val password:String

)


