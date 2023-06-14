package com.micro.account.utils

import com.micro.account.entity.Results
import com.micro.account.entity.model.Payload
import com.micro.account.entity.response.GeneralResponse
import com.micro.account.entity.response.TransactionHistoryResponse
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

    fun createTransactionHistoryResponseList(resultsList: List<Results>): List<TransactionHistoryResponse> {
        return resultsList.map { results ->
            TransactionHistoryResponse(
                transaction_id = results.id,
                tx_base_amount = results.tx_base_amount,
                from_account_Id = results.from_account_Id,
                to_account_id = results.to_account_Id,
                transaction_type_id = results.transaction_type_id,
                transaction_type = results.transaction_type,
                completed_date_utc = results.completed_date_utc
            )
        }
    }
}