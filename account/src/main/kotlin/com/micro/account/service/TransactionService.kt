package com.micro.account.service

import com.micro.account.entity.ErrorCode
import com.micro.account.entity.dto.P2PTransferRequestDto
import com.micro.account.entity.dto.TopUpCreditCardDto
import com.micro.account.entity.request.P2PTransferRequest
import com.micro.account.entity.request.TopUpCreditCardRequest
import com.micro.account.entity.request.TransactionHistoryRequest
import com.micro.account.entity.request.WalletInfoRequest
import com.micro.account.entity.response.CustomResponse
import com.micro.account.entity.response.P2PTransferResponse
import com.micro.account.repository.AccountRepository
import com.micro.account.utils.GeneralUtils
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime

@Service
class TransactionService(
    private val accountRepository: AccountRepository,
    private val tokenRetriever: TokenRetrieverService,
    private val accessTokenService: AccessTokenService
) {
    fun topUpCreditCard(request: TopUpCreditCardDto): ResponseEntity<Any> {
        val senderWalletNumber = accountRepository.findByAccountNumber(request.sender_account_number)?.walletNumber
        var accessToken = ""
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
            val restTemplate = RestTemplate()
            val requestUrl = "https://stsapiuat.walletgate.io/v1/Transaction/TopupCreditCard"
            val headers = HttpHeaders()
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            val requestBody = HttpEntity(topUpRequest, headers)
            val response =
                restTemplate.exchange(requestUrl, HttpMethod.POST, requestBody, P2PTransferResponse::class.java)
            val payload = response.body?.payload
            return ResponseEntity.ok(CustomResponse(payload, "TopUp Credit Card was successful"))
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
            val restTemplate = RestTemplate()
            val headers = HttpHeaders()
            if (account.walletId.isEmpty()) {
                headers.set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                val requestUrl = "https://stsapiuat.walletgate.io/v1/wallet/info"
                headers.set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                val requestBody = HttpEntity(WalletInfoRequest(account.accountNumber, account.walletNumber), headers)
                val response =
                    restTemplate.exchange(requestUrl, HttpMethod.POST, requestBody, P2PTransferResponse::class.java)
                val payload = response.body?.payload
                if (payload != null) {
                    account?.walletId = payload.wallet_info?.id.toString()
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
            val requestUrl = "https://stsapiuat.walletgate.io/v1/TransactionData/SummaryRecordByFilter"
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            val requestBody = HttpEntity(transactionHistoryRequest, headers)
            val response =
                restTemplate.exchange(requestUrl, HttpMethod.POST, requestBody, P2PTransferResponse::class.java)
            val payload = response.body?.payload
            if (payload != null) {
                return ResponseEntity.ok(
                    CustomResponse(
                        payload.results,
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
        var accessToken = ""
        if (senderWalletNumber != null
            && receiverWalletNumber != null
        ) {
            val request = P2PTransferRequest(
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
            val restTemplate = RestTemplate()
            val requestUrl = "https://stsapiuat.walletgate.io/v1/Transaction/PersonalToPersonalTransfer"
            val headers = HttpHeaders()
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            val requestBody = HttpEntity(request, headers)

            val response =
                restTemplate.exchange(requestUrl, HttpMethod.POST, requestBody, P2PTransferResponse::class.java)
            val payload = response.body?.payload
            var response2: CustomResponse<*>? = if (payload != null) {

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