package com.micro.account.entity

import com.micro.account.entity.model.Payload
import java.math.BigDecimal
import java.time.LocalDateTime

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

data class Results(
    val id: String?,
    val tenant_id: Int?,
    val tx_group_correlation_id: String?,
    val tx_ref_correlation_id: String?,
    val wallet_id: String?,
    val transaction_type_id: Int?,
    val transaction_type: String?,
    val transaction_status_id: Int?,
    val transaction_status: String?,
    val result_code: String?,
    val tx_additional_data_json: String?,
    val created_date_utc: LocalDateTime?,
    val updated_date_utc: LocalDateTime?,
    val completed_date_utc: LocalDateTime?,
    val financial_process_completed_date_utc: LocalDateTime?,
    val is_financial_process_completed: Boolean?,
    val to_wallet_id: String?,
    val tx_base_amount: BigDecimal?,
    val tx_additional_fee: BigDecimal?,
    val tx_amount_with_additional_fee: BigDecimal?,
    val currency_code: String?,
    val tx_end_user_preview_json: String?,
    val tx_pre_financial_acquired_record_json: String?,
    val tx_financial_acquired_record_json: String?,
    val from_description: String?,
    val to_description: String?,
    val kyc_level_id: Int?,
    val from_account_type_Id: Int?,
    val from_account_Id: String?,
    val from_wallet_number: String?,
    val to_account_number: String?,
    val to_account_type_Id: Int?,
    val to_account_Id: String?,
    val to_wallet_number: String?,
    val is_need_settlement: Boolean?,
    val settlement_day: Int?,
    val from_user_kyc_info: String?,
    val to_user_kyc_info: String?,
    val source_type: String?,
    val channel_type: String?,
    val media_identifier: String?,
    val terminal_no: String?,
    val media_type: String?,
    val provider_id: String?,
    val to_account_tx_base_amount: BigDecimal?,
    val to_account_tx_additional_fee: BigDecimal?,
    val to_account_tx_amount_with_additional_fee: BigDecimal?,

)


