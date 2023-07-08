package com.micro.account.service


import com.micro.account.entity.dto.KafkaAccountInfoDto
import com.micro.account.utils.GeneralUtils
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component

class AccountListener(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    private val logger: Logger = LoggerFactory.getLogger(AccountListener::class.java)

    @Autowired
    private lateinit var accountService: AccountService

    @Autowired
    private lateinit var accessTokenService: AccessTokenService

    @Autowired
    private lateinit var tokenRetrieverService: TokenRetrieverService

    @KafkaListener(topics = ["transaction_to_account_topUp"], groupId = "transaction-group")
    fun findByAccountNumber(request: ConsumerRecord<String, Any>) {
        val accountData = request.value() as Map<*, *>
        val accountNumber = accountData["accountNumber"] as String
        val account = accountService.findByAccountNumber(accountNumber)
        println("findByAccountNumber in account $accountNumber")
        if (account != null) {
            val accessToken: String
            if (accessTokenService.isAccessTokenExpired(accountNumber)) {
                accessToken = tokenRetrieverService.retrieveToken()
                accessTokenService.saveAccessToken(accessToken, accountNumber)
            } else {
                accessToken = accessTokenService.findByAccountNumber(accountNumber)?.accessToken
                    ?: tokenRetrieverService.retrieveToken()
            }
            kafkaTemplate.send(
                "account_to_transaction_topUp",
                KafkaAccountInfoDto(
                    accountNumber, account.walletNumber,
                    GeneralUtils.generateRandomNumber(), account.userFirstName, account.userLastName, accessToken
                )
            )
        } else {
            kafkaTemplate.send(
                "account_to_transaction_topUp",
                KafkaAccountInfoDto(
                    "0", "0",
                    "0", "0", "0", "0"
                )
            )
        }

    }

    @KafkaListener(topics = ["transaction_to_account_Personal_To_Personal_Transfer"], groupId = "transaction-group")
    fun sendFindAccountsByAccountNumbers(request: ConsumerRecord<String, Any>) {
        val walletInfoMap: HashMap<String, KafkaAccountInfoDto> = HashMap()
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
            walletInfoMap["sender_account"] = KafkaAccountInfoDto(
                senderAccountNumber,
                senderAccount.walletNumber,
                GeneralUtils.generateRandomNumber(),
                senderAccount.userFirstName,
                senderAccount.userLastName,
                accessToken
            )
            walletInfoMap["receiver_account"] = KafkaAccountInfoDto(
                receiverAccountNumber, senderAccount.walletNumber,
                GeneralUtils.generateRandomNumber(), receiverAccount.userFirstName, receiverAccount.userLastName, "0"
            )
            kafkaTemplate.send(
                "account_to_transaction_Personal_To_Personal_Transfer", walletInfoMap
            )
        } else {
            walletInfoMap["sender_account"] = KafkaAccountInfoDto(
                "0", "0",
                "0", "0", "0", "0"
            )
            walletInfoMap["receiver_account"] = KafkaAccountInfoDto(
                "0", "0",
                "0", "0", "0", "0"
            )
            kafkaTemplate.send(
                "account_to_transaction_Personal_To_Personal_Transfer", walletInfoMap
            )
        }

    }
}