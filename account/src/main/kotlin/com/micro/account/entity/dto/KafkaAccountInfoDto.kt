package com.micro.account.entity.dto

data class KafkaAccountInfoDto(
    var accountNumber: String,
    var walletNumber: String,
    var ext_transaction_id: String,
    var userFirstName: String,
    var userLastName: String,
    var accessToken: String
)