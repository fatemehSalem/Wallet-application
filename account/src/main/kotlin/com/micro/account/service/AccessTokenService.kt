package com.micro.account.service

import com.micro.account.entity.model.AccessToken
import com.micro.account.repository.AccessTokenRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AccessTokenService(private val accessTokenRepository: AccessTokenRepository) {
    fun saveAccessToken(token: String, userPhoneNumber: String) {
        val currentTime = Instant.now()
        val expirationTime = currentTime.plusSeconds(28800)

        val tokenEntity = AccessToken()
        tokenEntity.accessToken = token
        tokenEntity.userPhoneNumber = userPhoneNumber
        tokenEntity.creationTime = currentTime
        tokenEntity.expirationTime = expirationTime

        accessTokenRepository.save(tokenEntity)
    }

    fun isAccessTokenExpired(userPhoneNumber: String): Boolean {
        val tokenEntity = accessTokenRepository.findByUserPhoneNumber(userPhoneNumber)
            ?:
            return true

        val currentTime = Instant.now()
        val expirationTime = tokenEntity.expirationTime.minusSeconds(60)

        return currentTime.isAfter(expirationTime)
    }

    fun findByUserPhoneNumber(userPhoneNumber: String): AccessToken? {
        return accessTokenRepository.findByUserPhoneNumber(userPhoneNumber)
    }
}