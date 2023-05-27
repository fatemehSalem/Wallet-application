package com.micro.account.service


import com.micro.account.entity.Token
import com.micro.account.entity.TokenResponse
import com.micro.account.repository.TokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class TokenRetriever(private val webClient: WebClient) {
    @Autowired
    private lateinit var tokenRepository: TokenRepository
    fun retrieveToken(phoneNumber: String) {
        val client_id = "EE85AF95-EBD3-4D29-800C-3E8BE9340EBD"
        val client_secret = "D44C41B4-E71F-425F-BBCA-99585DC331DB"
        val requestBody = "grant_type=client_credentials&client_id=$client_id&client_secret=$client_secret"

        val tokenResponse: TokenResponse? = webClient.post()
            .uri("https://idsuat.walletgate.io/connect/token")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(TokenResponse::class.java)
            .block()
        if (tokenResponse != null) {
                val token = Token()
                token.accessToken = tokenResponse.access_token
                token.userPhoneNumber = phoneNumber
                tokenRepository.save(token)
               // return tokenResponse.access_token
        }
      //  return tokenResponse?.access_token ?: throw IllegalStateException("Token retrieval failed")
    }
}
