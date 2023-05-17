package com.micro.account.service

import com.micro.account.entity.Token
import com.micro.account.repository.TokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TokenService {
    @Autowired
    private lateinit var tokenRepository: TokenRepository

    fun findByUserPhoneNumber(userPhoneNumber: String): Token? {
        return  tokenRepository.findByUserPhoneNumber(userPhoneNumber)
    }
}