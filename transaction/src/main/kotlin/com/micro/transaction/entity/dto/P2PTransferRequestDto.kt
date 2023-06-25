package com.micro.transaction.entity.dto

import java.math.BigDecimal

data class P2PTransferRequestDto (
    val sender_account_number: String,
    val amount: BigDecimal,
    val receiver_account_number: String)