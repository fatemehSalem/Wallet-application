package com.micro.transaction.entity.dto

data class KafkaP2PTransferRequestDto(
    val sender_account_number: String,
    val receiver_account_number: String
)
