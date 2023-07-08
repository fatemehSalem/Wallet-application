package com.micro.transaction.service


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TransactionService() {
/*
    fun topUpCreditCard(request: TopUpCreditCardDto) {
        accountServices.sendFindAccountByAccountNumber(request)
    }*/
//    fun transactionHistory(accountNumber: String): ResponseEntity<Any> {
//        val account = accountServices.sendFindAccountByAccountNumberTemp(accountNumber)
//        val accessToken: String
//        if (account != null) {
//            if (accountServices.isAccessTokenExpired(accountNumber) == true) {
//                accessToken = accountServices.retrieveToken().toString()
//                accountServices.saveAccessToken(accessToken, accountNumber)
//            } else
//                accessToken = (accountServices.findAccessTokenByAccountNumber(accountNumber)?.accessToken
//                    ?: accountServices.retrieveToken()) as String
//            if (account.walletId.isEmpty()) {
//                val payload = GeneralUtils.runBackOfficeApI(
//                    "https://stsapiuat.walletgate.io/v1/wallet/info",
//                    accessToken, WalletInfoRequest(account.accountNumber, account.walletNumber)
//                )
//                if (payload != null) {
//                    account.walletId = payload.wallet_info?.id.toString()
//                    //account.updatedAt = LocalDateTime.now()
//                    accountServices.saveAccount(account)
//                }
//            }
//            val array: Array<Int> = arrayOf(1002, 2001, 3001)
//            val transactionHistoryRequest = TransactionHistoryRequest(
//                account.walletId.toLong(),
//                "2023-06-01T00:00",
//                LocalDateTime.now().toString(),
//                array.toList(),
//                100,
//                0,
//                "Id",
//                "asc"
//            )
//            val payload = GeneralUtils.runBackOfficeApI(
//                "https://stsapiuat.walletgate.io/v1/TransactionData/SummaryRecordByFilter",
//                accessToken, transactionHistoryRequest
//            )
//            if (payload != null) {
//                return ResponseEntity.ok(
//                    CustomResponse(
//                        payload.results?.let { createTransactionHistoryResponseList(it) },
//                        "Account get Transaction History was successful"
//                    )
//                )
//            }
//        }
//        val errorCode = ErrorCode.ACCOUNT_IS_NOT_FOUND
//        return ResponseEntity.ok(
//            CustomResponse(
//                null,
//                "Account get Transaction History was unsuccessful: Account Number is wrong",
//                errorCode.code
//            )
//        )
//    }
//
//    fun transactionDetail(transactionId: String, accountNumber: String): ResponseEntity<Any> {
//        val account = accountServices.sendFindAccountByAccountNumber(accountNumber)
//        val accessToken: String
//        if (account != null) {
//            if (accountServices.isAccessTokenExpired(accountNumber) == true) {
//                accessToken = accountServices.retrieveToken().toString()
//                accountServices.saveAccessToken(accessToken, accountNumber)
//            } else
//                accessToken = (accountServices.findAccessTokenByAccountNumber(accountNumber)?.accessToken
//                    ?: accountServices.retrieveToken()) as String
//            val transactionDetailRequest = TransactionDetailRequest(
//                transactionId,
//                "2023-06-01T00:00",
//                LocalDateTime.now().toString(),
//                100,
//                0,
//                "Id",
//                "asc"
//            )
//            val payload = GeneralUtils.runBackOfficeApI(
//                "https://stsapiuat.walletgate.io/v1/TransactionData/SummaryRecordByFilter",
//                accessToken, transactionDetailRequest
//            )
//            if (payload != null) {
//                return ResponseEntity.ok(
//                    CustomResponse(
//                        payload.results?.let { createTransactionDetailResponse(payload.results[0]) },
//                        "Account get Transaction History was successful"
//                    )
//                )
//            }
//        }
//        val errorCode = ErrorCode.ACCOUNT_IS_NOT_FOUND
//        return ResponseEntity.ok(
//            CustomResponse(
//                null,
//                "Account get Transaction History was unsuccessful: Account Number is wrong",
//                errorCode.code
//            )
//        )
//    }
//
//    fun personalToPersonalTransfer(request: P2PTransferRequestDto): ResponseEntity<Any> {
//        val senderAccount = accountServices.sendFindAccountByAccountNumberTemp(request.sender_account_number)
//        val receiverAccount = accountServices.sendFindAccountByAccountNumberTemp(request.receiver_account_number)
//        val accessToken: String
//        if (senderAccount != null
//            && receiverAccount != null
//        ) {
//            val p2pRequest = P2PTransferRequest(
//                request.sender_account_number,
//                senderAccount.walletNumber,
//                request.amount,
//                "TRY",
//                receiverAccount.walletNumber,
//                GeneralUtils.generateRandomNumber(),
//                "",
//                "",
//                "",
//                ""
//            )
//            if (accountServices.isAccessTokenExpired(request.sender_account_number) == true) {
//                accessToken = accountServices.retrieveToken().toString()
//                accountServices.saveAccessToken(accessToken, request.sender_account_number)
//            } else
//                accessToken = (accountServices.findAccessTokenByAccountNumber(request.sender_account_number)?.accessToken
//                    ?: accountServices.retrieveToken()) as String
//            val payload = GeneralUtils.runBackOfficeApI(
//                "https://stsapiuat.walletgate.io/v1/Transaction/PersonalToPersonalTransfer",
//                accessToken, p2pRequest
//            )
//            val response2: CustomResponse<*> = if (payload != null) {
//
//                CustomResponse(payload.transaction_amount?.let {
//                    P2pResponse(
//                        payload.transaction_id,
//                        it,
//                        senderAccount.userFirstName + " " + senderAccount.userFirstName,
//                        receiverAccount.userFirstName + " " + receiverAccount.userLastName,
//                        payload.sender_wallet_info?.created_date_utc
//                    )
//                }, "Personal To Personal Transfer was successful")
//            } else {
//                val errorCode = ErrorCode.PERSONAL_TO_PERSONAL_TRANSFER_WAS_UNSUCCESSFUL
//                CustomResponse(
//                    null, "Personal To Personal Transfer was unsuccessful",
//                    errorCode.code
//                )
//            }
//            return ResponseEntity.ok(response2)
//        }
//        val errorCode = ErrorCode.ACCOUNT_IS_NOT_FOUND
//        return ResponseEntity.ok(
//            CustomResponse(
//                null,
//                "Personal To Personal Transfer was unsuccessful: sender_account_number or receiver_account_number is wrong",
//                errorCode.code
//            )
//        )
//
//    }


}