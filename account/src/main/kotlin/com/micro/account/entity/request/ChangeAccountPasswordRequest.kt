package com.micro.account.entity.request

data class ChangeAccountPasswordRequest (val newPassword: String, val phoneNumber: String)