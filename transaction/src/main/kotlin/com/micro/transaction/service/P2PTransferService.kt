package com.micro.transaction.service

import com.micro.transaction.entity.ErrorCode
import com.micro.transaction.entity.dto.KafkaAccountInfoDto
import com.micro.transaction.entity.dto.KafkaP2PTransferRequestDto
import com.micro.transaction.entity.dto.P2PTransferRequestDto
import com.micro.transaction.entity.request.P2PTransferRequest
import com.micro.transaction.entity.response.CustomResponse
import com.micro.transaction.entity.response.P2pResponse
import com.micro.transaction.utils.GeneralUtils
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.http.ResponseEntity
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

@Service
class P2PTransferService(private val kafkaTemplate: KafkaTemplate<String, Any>) {
    private var responseFuture = CompletableFuture<ResponseEntity<*>>()
    private lateinit var amount: BigDecimal

    fun sendFindAccountsByAccountNumbers(request: P2PTransferRequestDto) {
        responseFuture = CompletableFuture<ResponseEntity<*>>()
        amount = request.amount
        kafkaTemplate.send(
            "transaction_to_account_Personal_To_Personal_Transfer",
            KafkaP2PTransferRequestDto(request.sender_account_number, request.receiver_account_number)
        )
    }

    @KafkaListener(topics = ["account_to_transaction_Personal_To_Personal_Transfer"], groupId = "transaction-group")
    fun listenToTopic(accountsInfoMap: ConsumerRecord<String, Any>) {
        val payload = accountsInfoMap.value()

        if (payload is Map<*, *>) {
            val walletInfoMap = payload as Map<*, *>
            val senderAccountInfoMap = walletInfoMap["sender_account"] as Map<String, Any>?
            val receiverAccountInfoMap = walletInfoMap["receiver_account"] as Map<String, Any>?

            val senderAccount = deserializeAccountInfoDto(senderAccountInfoMap)
            val receiverAccount = deserializeAccountInfoDto(receiverAccountInfoMap)

            println("Sender Account Info: $senderAccount")
            println("Receiver Account Info: $receiverAccount")

            if (senderAccount != null) {
                if (receiverAccount != null) {
                    if (senderAccount.accountNumber == "0" && receiverAccount.accountNumber == "0") {
                        val errorCode = ErrorCode.ACCOUNT_IS_NOT_FOUND
                        val result = ResponseEntity.ok(
                            CustomResponse(
                                null,
                                "Personal To Personal Transfer was unsuccessful: sender_account_number or receiver_account_number is wrong",
                                errorCode.code
                            )
                        )
                        responseFuture.complete(result)
                        return
                    } else {
                        val p2pRequest = P2PTransferRequest(
                            senderAccount.accountNumber,
                            senderAccount.walletNumber,
                            amount,
                            "TRY",
                            receiverAccount.walletNumber,
                            GeneralUtils.generateRandomNumber(),
                            "",
                            "",
                            "",
                            ""
                        )
                        val payload = GeneralUtils.runBackOfficeApI(
                            "https://stsapiuat.walletgate.io/v1/Transaction/PersonalToPersonalTransfer",
                            senderAccount.accessToken,
                            p2pRequest
                        )
                        if (payload != null) {
                            val result = ResponseEntity.ok(CustomResponse(payload.transaction_amount?.let {
                                P2pResponse(
                                    payload.transaction_id,
                                    it,
                                    senderAccount.userFirstName + " " + senderAccount.userFirstName,
                                    receiverAccount.userFirstName + " " + receiverAccount.userLastName,
                                    payload.sender_wallet_info?.created_date_utc
                                )
                            }, "Personal To Personal Transfer was successful"))
                            responseFuture.complete(result)
                            return
                        } else {
                            val errorCode = ErrorCode.PERSONAL_TO_PERSONAL_TRANSFER_WAS_UNSUCCESSFUL
                            val result = ResponseEntity.ok(
                                CustomResponse(
                                    null, "Personal To Personal Transfer was unsuccessful", errorCode.code
                                )
                            )
                            responseFuture.complete(result)
                            return
                        }

                    }
                }
            }

        } else {
            println("Received payload: $payload")
        }
    }

    fun deserializeAccountInfoDto(accountInfoMap: Map<String, Any>?): KafkaAccountInfoDto? {
        if (accountInfoMap != null) {
            val accountNumber = accountInfoMap["accountNumber"] as String
            val walletNumber = accountInfoMap["walletNumber"] as String
            val extTransactionId = accountInfoMap["ext_transaction_id"] as String
            val userFirstName = accountInfoMap["userFirstName"] as String
            val userLastName = accountInfoMap["userLastName"] as String
            val accessToken = accountInfoMap["accessToken"] as String

            return KafkaAccountInfoDto(
                accountNumber, walletNumber, extTransactionId, userFirstName, userLastName, accessToken
            )
        }
        return null
    }

    fun getResponseFuture(): CompletableFuture<ResponseEntity<*>> {
        return responseFuture
    }
}