package com.micro.account.entity.dto

import java.math.BigDecimal

data class GetAccountInfoResponseDto(
    val account_number: String,
    val first_name: String,
    val last_name: String,
    val current_balance: BigDecimal?,
    val monthly_balance_limit: BigDecimal?,
    val kyc_level_status: String,
    val kyc_level: String
)
