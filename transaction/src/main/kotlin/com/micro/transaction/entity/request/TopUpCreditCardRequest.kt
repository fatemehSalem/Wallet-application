package com.micro.transaction.entity.request

import java.math.BigDecimal

data class TopUpCreditCardRequest(
    val currency_code: String,
    val account_number: String,
    val credit_card_post_type_id: String,
    val ext_transaction_id: String,
    val wallet_number: String,
    val amount: BigDecimal,
    val hash_key: String,
    val source_type: String,
    val channel_type: String
)
