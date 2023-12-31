package com.micro.account.service

import com.micro.account.entity.model.AccessToken
import com.micro.account.repository.AccessTokenRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime

@Service
class AccessTokenService(private val accessTokenRepository: AccessTokenRepository) {
    fun saveAccessToken(token: String, accountNumber: String) {
        val currentTime = Instant.now()
        val expirationTime = currentTime.plusSeconds(28800)

        val tokenEntity = AccessToken()
        tokenEntity.accessToken = token
        tokenEntity.accountNumber = accountNumber
        tokenEntity.creationTime = currentTime
        tokenEntity.expirationTime = expirationTime
        tokenEntity.createdAt = LocalDateTime.now()
        accessTokenRepository.save(tokenEntity)
    }

    fun isAccessTokenExpired(accountNumber: String): Boolean {
        val tokenEntity = accessTokenRepository.findByAccountNumber(accountNumber)
            ?:
            return true

        val currentTime = Instant.now()
        val expirationTime = tokenEntity.expirationTime.minusSeconds(60)
        if(currentTime.isAfter(expirationTime)){
            accessTokenRepository.delete(tokenEntity)
        }
        return currentTime.isAfter(expirationTime)
    }

    fun findByAccountNumber(accountNumber: String): AccessToken? {
        return accessTokenRepository.findByAccountNumber(accountNumber)
    }
}