package com.micro.transaction.entity.request

import java.math.BigDecimal

data class P2PTransferRequest (
    val sender_account_number: String,
    val sender_wallet_number: String,
    val amount: BigDecimal,
    val currency_code: String,
    val receiver_wallet_number: String,
    val ext_transaction_id: String,

    val description: String,
    val hash_key: String,
    val source_type: String,
    val channel_type: String
)
