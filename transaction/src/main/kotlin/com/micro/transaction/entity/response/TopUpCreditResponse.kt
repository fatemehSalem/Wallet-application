package com.micro.transaction.entity.response

import java.math.BigDecimal

data class TopUpCreditResponse (
    val transaction_id: String?,
    val transaction_amount: BigDecimal,
    val sender_account_number: String?,
    val sender_account_name:String,
    val created_date_utc: String?
)
