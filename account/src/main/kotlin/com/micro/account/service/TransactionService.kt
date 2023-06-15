package com.micro.account.service

import com.micro.account.entity.ErrorCode
import com.micro.account.entity.dto.P2PTransferRequestDto
import com.micro.account.entity.dto.TopUpCreditCardDto
import com.micro.account.entity.request.*
import com.micro.account.entity.response.CustomResponse
import com.micro.account.repository.AccountRepository
import com.micro.account.utils.GeneralUtils
import com.micro.account.utils.GeneralUtils.createTransactionDetailResponse
import com.micro.account.utils.GeneralUtils.createTransactionHistoryResponseList
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TransactionService(
        private val accountRepository: AccountRepository,
        private val tokenRetriever: TokenRetrieverService,
        private val accessTokenService: AccessTokenService
) {
    fun topUpCreditCard(request: TopUpCreditCardDto): ResponseEntity<Any> {
        val senderWalletNumber = accountRepository.findByAccountNumber(request.sender_account_number)?.walletNumber
        val accessToken: String
        if (senderWalletNumber != null) {
            if (accessTokenService.isAccessTokenExpired(request.sender_account_number)) {
                accessToken = tokenRetriever.retrieveToken()

                accessTokenService.saveAccessToken(accessToken, request.sender_account_number)
            } else
                accessToken = accessTokenService.findByAccountNumber(request.sender_account_number)?.accessToken
                        ?: tokenRetriever.retrieveToken()
            val topUpRequest = TopUpCreditCardRequest(
                    "TRY",
                    request.sender_account_number,
                    "sipay_123",
                    GeneralUtils.generateRandomNumber(),
                    senderWalletNumber,
                    request.amount,
                    "",
                    "",
                    ""
            )
            val payload = GeneralUtils.runBackOfficeApI(
                    "https://stsapiuat.walletgate.io/v1/Transaction/TopupCreditCard",
                    accessToken, topUpRequest
            )
            if(payload!= null){
                return ResponseEntity.ok(CustomResponse(payload, "TopUp Credit Card was successful"))
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
        val account = accountRepository.findByAccountNumber(accountNumber)
        val accessToken: String
        if (account != null) {
            if (accessTokenService.isAccessTokenExpired(accountNumber)) {
                accessToken = tokenRetriever.retrieveToken()
                accessTokenService.saveAccessToken(accessToken, accountNumber)
            } else
                accessToken = accessTokenService.findByAccountNumber(accountNumber)?.accessToken
                        ?: tokenRetriever.retrieveToken()
            if (account.walletId.isEmpty()) {
                val payload = GeneralUtils.runBackOfficeApI(
                        "https://stsapiuat.walletgate.io/v1/wallet/info",
                        accessToken, WalletInfoRequest(account.accountNumber, account.walletNumber)
                )
                if (payload != null) {
                    account.walletId = payload.wallet_info?.id.toString()
                    account.updatedAt = LocalDateTime.now()
                    accountRepository.save(account)
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
        val account = accountRepository.findByAccountNumber(accountNumber)
        val accessToken: String
        if (account != null) {
            if (accessTokenService.isAccessTokenExpired(accountNumber)) {
                accessToken = tokenRetriever.retrieveToken()
                accessTokenService.saveAccessToken(accessToken, accountNumber)
            } else
                accessToken = accessTokenService.findByAccountNumber(accountNumber)?.accessToken
                        ?: tokenRetriever.retrieveToken()
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
        val senderWalletNumber = accountRepository.findByAccountNumber(request.sender_account_number)?.walletNumber
        val receiverWalletNumber = accountRepository.findByAccountNumber(request.receiver_account_number)?.walletNumber
        val accessToken: String
        if (senderWalletNumber != null
                && receiverWalletNumber != null
        ) {
            val p2pRequest = P2PTransferRequest(
                    request.sender_account_number,
                    senderWalletNumber,
                    request.amount,
                    "TRY",
                    receiverWalletNumber,
                    GeneralUtils.generateRandomNumber(),
                    "",
                    "",
                    "",
                    ""
            )
            if (accessTokenService.isAccessTokenExpired(request.sender_account_number)) {
                accessToken = tokenRetriever.retrieveToken()
                accessTokenService.saveAccessToken(accessToken, request.sender_account_number)
            } else
                accessToken = accessTokenService.findByAccountNumber(request.sender_account_number)?.accessToken
                        ?: tokenRetriever.retrieveToken()
            val payload = GeneralUtils.runBackOfficeApI(
                    "https://stsapiuat.walletgate.io/v1/Transaction/PersonalToPersonalTransfer",
                    accessToken, p2pRequest
            )
            val response2: CustomResponse<*> = if (payload != null) {

                CustomResponse(payload, "Personal To Personal Transfer was successful")
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