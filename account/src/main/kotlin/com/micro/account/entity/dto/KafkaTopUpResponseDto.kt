package com.micro.account.entity.dto

data class KafkaTopUpResponseDto(
    var accountNumber: String,
    var walletId: String?,
    var walletNumber: String,
    var ext_transaction_id: String,
    var userFirstName: String,
    var userLastName: String,
    var accessToken: String
)