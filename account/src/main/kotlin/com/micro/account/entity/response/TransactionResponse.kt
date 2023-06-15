package com.micro.account.entity.response

import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionResponse (
    val transaction_id: String?,
    val tx_base_amount: BigDecimal?,
    val to_account_number: String?,
    val to_description: String?,
    val from_description: String?,
    val transaction_type_id: Int?,
    val transaction_type: String?,
    val completed_date_utc: LocalDateTime?,
)