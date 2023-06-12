package com.micro.account.entity

import com.micro.account.entity.model.Payload
import java.math.BigDecimal

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
    val national_id: String?,
    val sector_id: Int?
)

data class SenderWalletInfo(
    val id: String?,
    val name: String?,
    val account_number: String?,
    val account_type: String?,
    val created_date_utc: String?,
    val number: String?,
    val phone_country_code: String?,
    val phone_number: String?,
    val email: String?,
    val total_balance: BigDecimal?,
    val monthly_incoming_total: BigDecimal?,
    val monthly_outgoing_total: BigDecimal?,
    val access_level_status_id: Int?,
    val access_level_status: String?,
    val payment_balance: PaymentBalance,
    val cash_balance: CashBalance,
    val transaction_limits: TransactionLimits,
    val kyc_level_status: String?,
    val currency_code: String?,
    val user_kyc_info: String?,
    val is_topup_default: Boolean?,
    val tax_number: String?,
    val loyalty_record_required: Boolean?,
)

data class PaymentBalance(
    val available: BigDecimal?,
    val unavailable: BigDecimal?,
)

data class CashBalance(
    val available: BigDecimal?,
    val unavailable: BigDecimal?,
)

data class TransactionLimits(
    val max_balance: BigDecimal?,
    val topup_credit_limit: BigDecimal?,
    val topup_cash_limit: BigDecimal?,
    val withdrawal_limit: BigDecimal?,
    val payment_limit: BigDecimal?,
    val wallet_to_wallet_limit: BigDecimal?,
)

data class WalletInfo(
    val id: String?,
    val name: String?,
    val account_number: String?,
    val account_type: String?,
    val created_date_utc: String?,
    val number: String?,
    val phone_country_code: String?,
    val phone_number: String?,
    val email: String?,
    val total_balance: BigDecimal?,
    val monthly_incoming_total: BigDecimal?,
    val monthly_outgoing_total: BigDecimal?,
    val access_level_status_id: Int?,
    val access_level_status: String?,
    val payment_balance: PaymentBalance?,
    val cash_balance: CashBalance?,
    val transaction_limits: TransactionLimits?,
    val user_kyc_info: UserKycInfo?
)


