package com.micro.transaction.service

import com.micro.transaction.entity.ErrorCode
import com.micro.transaction.entity.dto.TopUpCreditCardDto
import com.micro.transaction.entity.request.TransactionHistoryRequest
import com.micro.transaction.entity.response.CustomResponse
import com.micro.transaction.utils.GeneralUtils
import com.micro.transaction.utils.GeneralUtils.createTransactionHistoryResponseList
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.http.ResponseEntity
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@Service
class TransactionHistoryService(private val kafkaTemplate: KafkaTemplate<String, Any>) {
    private var responseFuture = CompletableFuture<ResponseEntity<*>>()

    fun sendFindAccountByAccountNumber(accountNumber: String) {
        responseFuture = CompletableFuture<ResponseEntity<*>>()
        kafkaTemplate.send("transaction_to_account_history", accountNumber)
    }

    @KafkaListener(topics = ["account_to_transaction_history"], groupId = "transaction-group")
    fun listenToTopic(account: ConsumerRecord<String, Any>) {
        val accountData = account.value() as Map<*, *>
        val walletId = accountData["walletId"] as String?
        val accessToken = accountData["accessToken"] as String
        if (walletId != null) {
            val array: Array<Int> = arrayOf(1002, 2001, 3001)
            val transactionHistoryRequest = TransactionHistoryRequest(
                walletId.toLong(),
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
                accessToken,
                transactionHistoryRequest
            )
            if (payload != null) {
                val result = ResponseEntity.ok(
                    CustomResponse(
                        payload.results?.let { createTransactionHistoryResponseList(it) },
                        "Account get Transaction History was successful"
                    )
                )
                responseFuture.complete(result)
                return
            }
            val errorCode = ErrorCode.ACCOUNT_IS_NOT_FOUND
            val result = ResponseEntity.ok(
                CustomResponse(
                    null, "Account get Transaction History was unsuccessful: Account Number is wrong", errorCode.code
                )
            )
            responseFuture.complete(result)
        }
    }
    fun getResponseFuture(): CompletableFuture<ResponseEntity<*>> {
        return responseFuture
    }
}
