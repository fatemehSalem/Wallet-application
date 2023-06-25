package com.micro.transaction.entity.request

data class TransactionDetailRequest(
    val id:String,
    val start_date: String,
    val end_date: String,
    val page_size: Int,
    val page_index: Int,
    val order_column: String,
    val order_by: String
)
