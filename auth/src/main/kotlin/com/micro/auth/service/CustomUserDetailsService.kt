package com.micro.auth.service

import com.micro.auth.model.AccountDto
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.core.userdetails.User
import org.springframework.web.client.RestTemplate

@Service
class CustomUserDetailsService(private val restTemplate: RestTemplate) : UserDetailsService {

    override fun loadUserByUsername(phoneNumber: String): UserDetails {

            val url = "http://account/api/accounts/{phoneNumber}"
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            }
            val httpMethod = HttpMethod.GET

            val response = restTemplate.exchange(url, httpMethod, null, AccountDto::class.java, phoneNumber)
            var accountDto = AccountDto("", "")
            if (response.statusCode.is2xxSuccessful) {
                accountDto = response.body!!
            }
            return User(
                accountDto.phoneNumberOrEmail,
                accountDto.password,
                true,
                true,
                true,
                true,
                emptyList()
            )

    }
}