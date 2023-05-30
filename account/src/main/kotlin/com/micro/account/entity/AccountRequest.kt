package com.micro.account.entity

data class AccountRequest(
    val account_number: String,
    val currency_code: String,
    val alias: String,
    val user_number: String,
    val user_first_name: String,
    val user_last_name: String,
    val user_phone_country_code: String,
    val user_phone_number: String,
    val user_email: String,
    val password:String

)


