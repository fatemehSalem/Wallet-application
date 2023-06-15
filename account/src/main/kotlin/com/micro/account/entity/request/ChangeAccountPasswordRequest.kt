package com.micro.account.entity.request

data class ChangeAccountPasswordRequest (val user_new_password: String, val user_phone_number: String)