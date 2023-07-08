package com.micro.account.service


import com.micro.account.entity.dto.KafkaTopUpResponseDto
import com.micro.account.entity.request.WalletInfoRequest
import com.micro.account.utils.GeneralUtils
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component

class AccountListener(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    @Autowired
    private lateinit var accountService: AccountService

    @Autowired
    private lateinit var accessTokenService: AccessTokenService

    @Autowired
    private lateinit var tokenRetrieverService: TokenRetrieverService

    @KafkaListener(
        topics = ["transaction_to_account_topUp", "transaction_to_account_history", "transaction_to_account_detail"],
        groupId = "transaction-group"
    )
    fun findByAccountNumber(request: ConsumerRecord<String, Any>) {
        val topic = request.topic()
        val accountNumber = request.value() as String
        val account = accountService.findByAccountNumber(accountNumber)
        println("findByAccountNumber in account $accountNumber")
        var kafkaTopic: String = ""
        if (account != null) {
            val accessToken: String
            if (accessTokenService.isAccessTokenExpired(accountNumber)) {
                accessToken = tokenRetrieverService.retrieveToken()
                accessTokenService.saveAccessToken(accessToken, accountNumber)
            } else {
                accessToken = accessTokenService.findByAccountNumber(accountNumber)?.accessToken
                    ?: tokenRetrieverService.retrieveToken()
            }
            when (topic) {
                "transaction_to_account_topUp" -> {
                    kafkaTemplate.send(
                        "account_to_transaction_topUp", KafkaTopUpResponseDto(
                            accountNumber,
                            account.walletId,
                            account.walletNumber,
                            GeneralUtils.generateRandomNumber(),
                            account.userFirstName,
                            account.userLastName,
                            accessToken
                        )
                    )
                }
                "transaction_to_account_history" -> {
                    if (account.walletId.isEmpty()) {
                        val payload = GeneralUtils.runBackOfficeApI(
                            "https://stsapiuat.walletgate.io/v1/wallet/info",
                            accessToken,
                            WalletInfoRequest(account.accountNumber, account.walletNumber)
                        )
                        if (payload != null) {
                            account.walletId = payload.wallet_info?.id.toString()
                            account.updatedAt = LocalDateTime.now()
                            accountService.saveAccount(account)
                        }
                    }
                    kafkaTemplate.send(
                        "account_to_transaction_history", KafkaTopUpResponseDto(
                            accountNumber,
                            account.walletId,
                            account.walletNumber,
                            GeneralUtils.generateRandomNumber(),
                            account.userFirstName,
                            account.userLastName,
                            accessToken
                        )
                    )
                }
                "transaction_to_account_detail" -> {
                    kafkaTemplate.send(
                        "account_to_transaction_detail", KafkaTopUpResponseDto(
                            accountNumber,
                            account.walletId,
                            account.walletNumber,
                            GeneralUtils.generateRandomNumber(),
                            account.userFirstName,
                            account.userLastName,
                            accessToken
                        )
                    )
                }
                else -> {
                    println("Received message from unknown topic in account Listener: $topic")
                }
            }

        } else {
            when (topic) {
                "transaction_to_account_topUp" -> {
                    kafkaTopic = "account_to_transaction_topUp"
                }
                "transaction_to_account_history" -> {
                    kafkaTopic = "account_to_transaction_history"
                }
                "transaction_to_account_detail" -> {
                    kafkaTopic = "account_to_transaction_detail"
                }
                else -> {
                    println("Received message from unknown topic in account Listener: $topic")
                }
            }
            kafkaTemplate.send(
                kafkaTopic, KafkaTopUpResponseDto(
                    "0", "0", "0", "0", "0", "0", "0"
                )
            )
        }

    }

    @KafkaListener(topics = ["transaction_to_account_Personal_To_Personal_Transfer"], groupId = "transaction-group")
    fun sendFindAccountsByAccountNumbers(request: ConsumerRecord<String, Any>) {
        val walletInfoMap: HashMap<String, KafkaTopUpResponseDto> = HashMap()
        val accountData = request.value() as Map<*, *>
        val senderAccountNumber = accountData["sender_account_number"] as String
        val receiverAccountNumber = accountData["receiver_account_number"] as String

        val senderAccount = accountService.findByAccountNumber(senderAccountNumber)
        val receiverAccount = accountService.findByAccountNumber(receiverAccountNumber)

        if (senderAccount != null && receiverAccount != null) {
            val accessToken: String
            if (accessTokenService.isAccessTokenExpired(senderAccountNumber)) {
                accessToken = tokenRetrieverService.retrieveToken()
                accessTokenService.saveAccessToken(accessToken, senderAccountNumber)
            } else {
                accessToken = accessTokenService.findByAccountNumber(senderAccountNumber)?.accessToken
                    ?: tokenRetrieverService.retrieveToken()
            }
            walletInfoMap["sender_account"] = KafkaTopUpResponseDto(
                senderAccountNumber,
                senderAccount.walletId,
                senderAccount.walletNumber,
                GeneralUtils.generateRandomNumber(),
                senderAccount.userFirstName,
                senderAccount.userLastName,
                accessToken
            )
            walletInfoMap["receiver_account"] = KafkaTopUpResponseDto(
                receiverAccountNumber,
                receiverAccount.walletId,
                receiverAccount.walletNumber,
                GeneralUtils.generateRandomNumber(),
                receiverAccount.userFirstName,
                receiverAccount.userLastName,
                "0"
            )
            kafkaTemplate.send(
                "account_to_transaction_Personal_To_Personal_Transfer", walletInfoMap
            )
        } else {
            walletInfoMap["sender_account"] = KafkaTopUpResponseDto(
                "0", "0", "0", "0", "0", "0", "0"
            )
            walletInfoMap["receiver_account"] = KafkaTopUpResponseDto(
                "0", "0", "0", "0", "0", "0", "0"
            )
            kafkaTemplate.send(
                "account_to_transaction_Personal_To_Personal_Transfer", walletInfoMap
            )
        }
    }
}