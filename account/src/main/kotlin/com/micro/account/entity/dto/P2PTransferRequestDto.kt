package com.micro.account.entity.dto

import java.math.BigDecimal

data class P2PTransferRequestDto (
    val sender_account_number: String,
    val sender_wallet_number: String,
    val amount: BigDecimal,
    val receiver_wallet_number: String)