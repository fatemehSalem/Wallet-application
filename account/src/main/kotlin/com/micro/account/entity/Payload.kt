package com.micro.account.entity

data class Payload(
    val user_number: String?,
    val wallet_number: String?,
    val kyc_level_code: Int?,
    val id: String?,
    val tenant_id: String?,
    val account_number: String?,
    val alias: String?,
    val kyc_level: String?,
    val owner_user_id: String?,
    val phone_country_code: String?,
    val phone_number: String?,
    val email: String?,
    val user_kyc_info: UserKycInfo?,
    val access_level_status_id: Int?,
    val created_date_utc: String?,
    val updated_date_utc: String?,
    val contact_address_contact_first_name: String?,
    val contact_address_contact_last_name: String?,
    val contact_address_contact_email: String?,
    val contact_address_contact_phone: String?,
    val contact_address_address_line1: String?,
    val contact_address_address_line2: String?,
    val contact_address_zip_postal_code: String?,
    val contact_address_state_province_code: String?,
    val contact_address_country_code: String?,
    val last_activity_date_utc: String?,
    val last_failed_login_date_utc: String?
)
