package com.micro.transaction.service

import com.micro.transaction.entity.ErrorCode
import com.micro.transaction.entity.dto.P2PTransferRequestDto
import com.micro.transaction.entity.dto.TopUpCreditCardDto
import com.micro.transaction.entity.request.*
import com.micro.transaction.entity.response.CustomResponse
import com.micro.transaction.entity.response.P2pResponse
import com.micro.transaction.entity.response.TopUpCreditResponse
import com.micro.transaction.utils.GeneralUtils
import com.micro.transaction.utils.GeneralUtils.createTransactionDetailResponse
import com.micro.transaction.utils.GeneralUtils.createTransactionHistoryResponseList
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TransactionService(
    private val accountServices: CallAccountServices
) {
    fun topUpCreditCard(request: TopUpCreditCardDto): ResponseEntity<Any> {
        val account = accountServices.findByAccountNumber(request.sender_account_number)
        val accessToken: String
        if (account != null) {
            if (accountServices.isAccessTokenExpired(request.sender_account_number) == true) {
                accessToken = accountServices.retrieveToken().toString()

                accountServices.saveAccessToken(accessToken, request.sender_account_number)
            } else
                accessToken = (accountServices.findAccessTokenByAccountNumber(request.sender_account_number)?.accessToken
                    ?: accountServices.retrieveToken()) as String
            val topUpRequest = TopUpCreditCardRequest(
                "TRY",
                request.sender_account_number,
                "sipay_123",
                GeneralUtils.generateRandomNumber(),
                account.walletNumber,
                request.amount,
                "",
                "",
                ""
            )
            val payload = GeneralUtils.runBackOfficeApI(
                "https://stsapiuat.walletgate.io/v1/Transaction/TopupCreditCard",
                accessToken, topUpRequest
            )
            if (payload != null) {

                return ResponseEntity.ok(CustomResponse(payload.transaction_amount?.let {
                    TopUpCreditResponse(
                        payload.transaction_id,
                        it,
                        payload.wallet_info?.account_number,
                        account.userFirstName + " " + account.userLastName,
                        payload.wallet_info?.created_date_utc
                    )
                }, "TopUp Credit Card was successful"))
            }

            val errorCode = ErrorCode.TOP_UP_TRANSACTION_WAS_UNSUCCESSFUL
            return ResponseEntity.ok(
                CustomResponse(
                    null,
                    "TopUp Credit Card was unsuccessful: the amount is higher than the allowed amount",
                    errorCode.code
                )
            )

        }
        val errorCode = ErrorCode.ACCOUNT_IS_NOT_FOUND
        return ResponseEntity.ok(
            CustomResponse(
                null,
                "TopUp Credit Card was unsuccessful: sender_account_number is wrong",
                errorCode.code
            )
        )
    }

    fun transactionHistory(accountNumber: String): ResponseEntity<Any> {
        val account = accountServices.findByAccountNumber(accountNumber)
        val accessToken: String
        if (account != null) {
            if (accountServices.isAccessTokenExpired(accountNumber) == true) {
                accessToken = accountServices.retrieveToken().toString()
                accountServices.saveAccessToken(accessToken, accountNumber)
            } else
                accessToken = (accountServices.findAccessTokenByAccountNumber(accountNumber)?.accessToken
                    ?: accountServices.retrieveToken()) as String
            if (account.walletId.isEmpty()) {
                val payload = GeneralUtils.runBackOfficeApI(
                    "https://stsapiuat.walletgate.io/v1/wallet/info",
                    accessToken, WalletInfoRequest(account.accountNumber, account.walletNumber)
                )
                if (payload != null) {
                    account.walletId = payload.wallet_info?.id.toString()
                    account.updatedAt = LocalDateTime.now()
                    accountServices.saveAccount(account)
                }
            }
            val array: Array<Int> = arrayOf(1002, 2001, 3001)
            val transactionHistoryRequest = TransactionHistoryRequest(
                account.walletId.toLong(),
                "2023-06-01T00:00",
                LocalDateTime.now().toString(),
                array.toList(),
                100,
                0,
                "Id",
                "asc"
            )
            val payload = GeneralUtils.runBackOfficeApI(
                "https://stsapiuat.walletgate.io/v1/TransactionData/SummaryRecordByFilter",
                accessToken, transactionHistoryRequest
            )
            if (payload != null) {
                return ResponseEntity.ok(
                    CustomResponse(
                        payload.results?.let { createTransactionHistoryResponseList(it) },
                        "Account get Transaction History was successful"
                    )
                )
            }
        }
        val errorCode = ErrorCode.ACCOUNT_IS_NOT_FOUND
        return ResponseEntity.ok(
            CustomResponse(
                null,
                "Account get Transaction History was unsuccessful: Account Number is wrong",
                errorCode.code
            )
        )
    }

    fun transactionDetail(transactionId: String, accountNumber: String): ResponseEntity<Any> {
        val account = accountServices.findByAccountNumber(accountNumber)
        val accessToken: String
        if (account != null) {
            if (accountServices.isAccessTokenExpired(accountNumber) == true) {
                accessToken = accountServices.retrieveToken().toString()
                accountServices.saveAccessToken(accessToken, accountNumber)
            } else
                accessToken = (accountServices.findAccessTokenByAccountNumber(accountNumber)?.accessToken
                    ?: accountServices.retrieveToken()) as String
            val transactionDetailRequest = TransactionDetailRequest(
                transactionId,
                "2023-06-01T00:00",
                LocalDateTime.now().toString(),
                100,
                0,
                "Id",
                "asc"
            )
            val payload = GeneralUtils.runBackOfficeApI(
                "https://stsapiuat.walletgate.io/v1/TransactionData/SummaryRecordByFilter",
                accessToken, transactionDetailRequest
            )
            if (payload != null) {
                return ResponseEntity.ok(
                    CustomResponse(
                        payload.results?.let { createTransactionDetailResponse(payload.results[0]) },
                        "Account get Transaction History was successful"
                    )
                )
            }
        }
        val errorCode = ErrorCode.ACCOUNT_IS_NOT_FOUND
        return ResponseEntity.ok(
            CustomResponse(
                null,
                "Account get Transaction History was unsuccessful: Account Number is wrong",
                errorCode.code
            )
        )
    }

    fun personalToPersonalTransfer(request: P2PTransferRequestDto): ResponseEntity<Any> {
        val senderAccount = accountServices.findByAccountNumber(request.sender_account_number)
        val receiverAccount = accountServices.findByAccountNumber(request.receiver_account_number)
        val accessToken: String
        if (senderAccount != null
            && receiverAccount != null
        ) {
            val p2pRequest = P2PTransferRequest(
                request.sender_account_number,
                senderAccount.walletNumber,
                request.amount,
                "TRY",
                receiverAccount.walletNumber,
                GeneralUtils.generateRandomNumber(),
                "",
                "",
                "",
                ""
            )
            if (accountServices.isAccessTokenExpired(request.sender_account_number) == true) {
                accessToken = accountServices.retrieveToken().toString()
                accountServices.saveAccessToken(accessToken, request.sender_account_number)
            } else
                accessToken = (accountServices.findAccessTokenByAccountNumber(request.sender_account_number)?.accessToken
                    ?: accountServices.retrieveToken()) as String
            val payload = GeneralUtils.runBackOfficeApI(
                "https://stsapiuat.walletgate.io/v1/Transaction/PersonalToPersonalTransfer",
                accessToken, p2pRequest
            )
            val response2: CustomResponse<*> = if (payload != null) {

                CustomResponse(payload.transaction_amount?.let {
                    P2pResponse(
                        payload.transaction_id,
                        it,
                        senderAccount.userFirstName + " " + senderAccount.userFirstName,
                        receiverAccount.userFirstName + " " + receiverAccount.userLastName,
                        payload.sender_wallet_info?.created_date_utc
                    )
                }, "Personal To Personal Transfer was successful")
            } else {
                val errorCode = ErrorCode.PERSONAL_TO_PERSONAL_TRANSFER_WAS_UNSUCCESSFUL
                CustomResponse(
                    null, "Personal To Personal Transfer was unsuccessful",
                    errorCode.code
                )
            }
            return ResponseEntity.ok(response2)
        }
        val errorCode = ErrorCode.ACCOUNT_IS_NOT_FOUND
        return ResponseEntity.ok(
            CustomResponse(
                null,
                "Personal To Personal Transfer was unsuccessful: sender_account_number or receiver_account_number is wrong",
                errorCode.code
            )
        )

    }


}