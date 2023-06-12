package com.micro.account.entity.dto

import java.math.BigDecimal

data class TopUpCreditCardDto (
    val sender_account_number: String,
    val amount: BigDecimal
        )