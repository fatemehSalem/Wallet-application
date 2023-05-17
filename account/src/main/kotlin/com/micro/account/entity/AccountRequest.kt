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
    val contact_address: ContactAddress
)

data class ContactAddress(
    val first_name: String,
    val last_name: String,
    val email: String,
    val address_line1: String,
    val address_line2: String,
    val zip_postal_code: String,
    val phone_number: String,
    val state_province_code: String,
    val country_code: String
)