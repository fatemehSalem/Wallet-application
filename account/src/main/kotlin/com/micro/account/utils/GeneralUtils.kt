package com.micro.account.utils

import com.micro.account.entity.Results
import com.micro.account.entity.model.Payload
import com.micro.account.entity.response.GeneralResponse
import com.micro.account.entity.response.TransactionResponse
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import kotlin.random.Random

object GeneralUtils {
/*    fun generateRandomString(): String {
        val length = Random.nextInt(8, 11)
        val chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }*/

    fun splitLastFourDigits(phoneNumber: String): String {
        val lastIndex = phoneNumber.length - 1
        return if (lastIndex >= 3) {
            phoneNumber.substring(lastIndex - 3)
        } else {
            phoneNumber
        }
    }

    fun generateRandomNumber(): String {
        val random = Random(System.currentTimeMillis())
        return random.nextInt(10000000, 99999999).toString()
    }

    fun runBackOfficeApI(requestUrl: String, accessToken: String, request: Any): Payload? {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        val requestBody = HttpEntity(request, headers)
        val response =
                restTemplate.exchange(requestUrl, HttpMethod.POST, requestBody, GeneralResponse::class.java)
        return response.body?.payload
    }

    fun createTransactionHistoryResponseList(resultsList: List<Results>): List<TransactionResponse> {
        return resultsList.map { results ->
            TransactionResponse(
                    transaction_id = results.id,
                    tx_base_amount = results.tx_base_amount,
                    to_account_number = results.to_account_number,
                    to_account_name = results.to_description,
                    from_account_name = results.from_description,
                    transaction_type_id = results.transaction_type_id,
                    transaction_type = results.transaction_type,
                    completed_date_utc = results.completed_date_utc
            )
        }
    }

    fun createTransactionDetailResponse(result: Results): TransactionResponse {
        return TransactionResponse(
                transaction_id = result.id,
                tx_base_amount = result.tx_base_amount,
                to_account_number = result.to_account_number,
                to_account_name = result.to_description,
                from_account_name = result.from_description,
                transaction_type_id = result.transaction_type_id,
                transaction_type = result.transaction_type,
                completed_date_utc = result.completed_date_utc
        )
    }

}