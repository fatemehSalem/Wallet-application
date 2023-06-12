package com.micro.account.entity.dto

import java.math.BigDecimal

data class GetAccountInfoResponseDto(
    val account_number: String,
    val first_name: String,
    val last_name: String,
    val max_balance: BigDecimal?,
    val kyc_level_status: String

)
