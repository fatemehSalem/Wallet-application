package com.micro.account.entity.request

data class TransactionHistoryRequest(
    val wallet_id: Long,
    val start_date: String,
    val end_date: String,
    val transaction_type_code_list: List<Int>,
    val page_size: Int,
    val page_index: Int,
    val order_column: String,
    val order_by: String
)
