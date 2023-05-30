package com.micro.account.entity

data class AccountResponse (
    val status: String,
    val code: String,
    val message: String,
    val payload: Payload
)
data class Payload(
    val account_number: String,
    val user_number: String,
    val wallet_number: String,
    val kyc_level_code: String
)