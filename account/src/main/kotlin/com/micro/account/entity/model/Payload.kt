package com.micro.account.entity.model

import com.micro.account.entity.SenderWalletInfo
import com.micro.account.entity.UserKycInfo
import com.micro.account.entity.WalletInfo
import java.math.BigDecimal

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
    val last_failed_login_date_utc: String?,
    val transaction_id: String?,
    val transaction_amount: BigDecimal?,
    val receiver_first_name: String?,
    val receiver_last_name: String?,
    val receiver_account_number: String?,
    val sender_wallet_info: SenderWalletInfo?,
    val ext_transaction_id: String?,
    val transaction_fee_amount: BigDecimal?,
    val previous_amount: BigDecimal?,
    val wallet_info: WalletInfo?,


    )
