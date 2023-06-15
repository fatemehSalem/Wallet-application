package com.micro.account.entity.response

import java.math.BigDecimal

data class P2pResponse(
    val transaction_id: String?,
    val transaction_amount: BigDecimal,
    val sender_account_name: String,
    val receiver_account_name: String,
    val created_date_utc: String?
)
