package com.micro.transaction.service

import com.micro.transaction.entity.ErrorCode
import com.micro.transaction.entity.dto.TopUpCreditCardDto
import com.micro.transaction.entity.request.TopUpCreditCardRequest
import com.micro.transaction.entity.response.CustomResponse
import com.micro.transaction.entity.response.TopUpCreditResponse
import com.micro.transaction.utils.GeneralUtils
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

@Service
class TopUpCreditCardService(private val kafkaTemplate: KafkaTemplate<String, Any>) {

    private lateinit var amount: BigDecimal
    private var responseFuture = CompletableFuture<ResponseEntity<*>>()

    fun sendFindAccountByAccountNumber(request: TopUpCreditCardDto) {
        responseFuture = CompletableFuture<ResponseEntity<*>>()
        amount = request.amount
        kafkaTemplate.send("transaction_to_account_topUp", request.sender_account_number)
    }

    @KafkaListener(topics = ["account_to_transaction_topUp"], groupId = "transaction-group")
    fun listenToTopic(account: ConsumerRecord<String, Any>) {
        val accountData = account.value() as Map<*, *>
        val accountNumber = accountData["accountNumber"] as String
        val accessToken = accountData["accessToken"] as String
        val extTransactionId = accountData["ext_transaction_id"] as String
        val walletNumber = accountData["walletNumber"] as String
        val userFirstName = accountData["userFirstName"] as String
        val userLastName = accountData["userLastName"] as String

        if (accountNumber != "0") {
            val topUpRequest = TopUpCreditCardRequest(
                "TRY", accountNumber, "sipay_123", extTransactionId, walletNumber, amount, "", "", ""
            )
            val payload = GeneralUtils.runBackOfficeApI(
                "https://stsapiuat.walletgate.io/v1/Transaction/TopupCreditCard", accessToken, topUpRequest
            )
            if (payload != null) {
                val result = ResponseEntity.ok(CustomResponse(payload.transaction_amount?.let {
                    TopUpCreditResponse(
                        payload.transaction_id,
                        it,
                        payload.wallet_info?.account_number,
                        "$userFirstName $userLastName",
                        payload.wallet_info?.created_date_utc
                    )
                }, "TopUp Credit Card was successful"))
                responseFuture.complete(result)
                return
            }
            val errorCode = ErrorCode.TOP_UP_TRANSACTION_WAS_UNSUCCESSFUL
            val result = ResponseEntity.ok(
                CustomResponse(
                    null,
                    "TopUp Credit Card was unsuccessful: the amount is higher than the allowed amount",
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

