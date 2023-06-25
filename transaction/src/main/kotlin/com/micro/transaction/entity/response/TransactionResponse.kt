package com.micro.transaction.entity.response

import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionResponse (
    val transaction_id: String?,
    val tx_base_amount: BigDecimal?,
    val balance_after_transaction: String?,
    val to_account_number: String?,
    val to_account_name: String?,
    val from_account_name: String?,
    val transaction_type_id: Int?,
    val transaction_type: String?,
    val completed_date_utc: LocalDateTime?,
)
