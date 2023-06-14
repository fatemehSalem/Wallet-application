package com.micro.account.entity.response

import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionHistoryResponse (
    val transaction_id: String?,
    val tx_base_amount: BigDecimal?,
    val from_account_Id: String?,
    val to_account_id: String?,
    val transaction_type_id: Int?,
    val transaction_type: String?,
    val completed_date_utc: LocalDateTime?,
)