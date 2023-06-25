package com.micro.transaction.service

import com.micro.transaction.entity.model.AccessToken
import com.micro.transaction.entity.model.Account
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType

@Service
class CallAccountServices(private val restTemplate: RestTemplate) {
    fun findByAccountNumber(accountNumber: String): Account? {
        val url = "http://localhost:8081/api/account/findByAccountNumber/$accountNumber"
        return restTemplate.getForObject(url, Account::class.java)
    }

    fun saveAccount(account:Account): Account? {
        val url = "http://localhost:8081/api/account/saveAccount"
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val requestEntity = HttpEntity(account, headers)
        val responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Account::class.java)
        return responseEntity.body
    }

    fun retrieveToken(): String? {
        val url = "http://localhost:8081/api/account/retrieveToken"
        return restTemplate.getForObject(url, String::class.java)
    }

    fun findAccessTokenByAccountNumber(accountNumber: String): AccessToken? {
        val url = "http://localhost:8081/api/account/accessTokenByAccountNumber"
        return restTemplate.getForObject(url, AccessToken::class.java)
    }

    fun isAccessTokenExpired(accountNumber: String):Boolean?{
        val url = "http://localhost:8081/api/account/isAccessTokenExpired"
        return restTemplate.getForObject(url, Boolean::class.java)
    }

    fun saveAccessToken(token: String, accountNumber: String) {
        val url = "http://localhost:8081/api/accounts/saveAccessToken/$token/$accountNumber"
        restTemplate.getForObject(url, Unit::class.java)
    }
}