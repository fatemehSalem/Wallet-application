package com.micro.account.service


import com.micro.account.entity.response.TokenResponse
import com.micro.account.repository.AccessTokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class TokenRetriever() {
    @Autowired
    private lateinit var tokenRepository: AccessTokenRepository


    fun retrieveToken(): String {
        val client_id = "EE85AF95-EBD3-4D29-800C-3E8BE9340EBD"
        val client_secret = "D44C41B4-E71F-425F-BBCA-99585DC331DB"
        val tokenEndpoint = "https://idsuat.walletgate.io/connect/token"

        // Prepare the request body
        val requestBody = LinkedMultiValueMap<String, String>()
        requestBody.add("grant_type", "client_credentials")
        requestBody.add("client_id", client_id)
        requestBody.add("client_secret", client_secret)

        // Prepare the request headers
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        // Create the HTTP entity with request body and headers
        val requestEntity = HttpEntity(requestBody, headers)

        // Create a RestTemplate instance
        val restTemplate = RestTemplate()

        // Send the POST request to the token endpoint
        val responseEntity = restTemplate.exchange(
            tokenEndpoint,
            HttpMethod.POST,
            requestEntity,
            TokenResponse::class.java
        )

        // Retrieve the response body
        val tokenResponse = responseEntity.body

        return tokenResponse?.access_token ?: throw IllegalStateException("Token retrieval failed")
    }

}
