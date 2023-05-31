package com.micro.account.entity
data class GetAccountInfoResponse(
    val status: Int?,
    val code: String?,
    val message: String?,
    val payload: Payload?
)
data class UserKycInfo(
    val first_name: String?,
    val last_name: String?,
    val birth_year: Int?,
    val kyc_level: Int?,
    val kyc_level_status: String?,
    val national_id: String?
)


