package com.micro.transaction.service

import com.micro.transaction.entity.ErrorCode
import com.micro.transaction.entity.request.TransactionDetailRequest
import com.micro.transaction.entity.response.CustomResponse
import com.micro.transaction.utils.GeneralUtils
import com.micro.transaction.utils.GeneralUtils.createTransactionDetailResponse
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.http.ResponseEntity
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@Service
class TransactionDetailService(private val kafkaTemplate: KafkaTemplate<String, Any>) {
    private var responseFuture = CompletableFuture<ResponseEntity<*>>()
    private lateinit var transactionId: String

    fun sendFindAccountByAccountNumber(tId: String, accountNumber: String) {
        responseFuture = CompletableFuture<ResponseEntity<*>>()
        transactionId = tId
        kafkaTemplate.send("transaction_to_account_detail", accountNumber)
    }

    @KafkaListener(topics = ["account_to_transaction_detail"], groupId = "transaction-group")
    fun listenToTopic(account: ConsumerRecord<String, Any>) {
        val accountData = account.value() as Map<*, *>
        val accountNumber = accountData["accountNumber"] as String
        val accessToken = accountData["accessToken"] as String
        if (accountNumber != "0") {
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
                val result = ResponseEntity.ok(
                    CustomResponse(
                        payload.results?.let { createTransactionDetailResponse(payload.results[0]) },
                        "Account get Transaction History was successful"
                    )
                )
                responseFuture.complete(result)
                return
            }
            val errorCode = ErrorCode.ACCOUNT_IS_NOT_FOUND
            val result = ResponseEntity.ok(
                CustomResponse(
                    null,
                    "Account get Transaction History was unsuccessful: Account Number is wrong",
                    errorCode.code
                )
            )
            responseFuture.complete(result)
            return
        }
        val errorCode = ErrorCode.ACCOUNT_IS_NOT_FOUND
        val result = ResponseEntity.ok(
            CustomResponse(
                null, "TopUp Credit Card was unsuccessful: sender_account_number is wrong", errorCode.code
            )
        )
        responseFuture.complete(result)

    }

    fun getResponseFuture(): CompletableFuture<ResponseEntity<*>> {
        return responseFuture
    }
}