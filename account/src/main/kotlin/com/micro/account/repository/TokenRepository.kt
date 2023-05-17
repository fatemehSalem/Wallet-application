package com.micro.account.repository

import com.micro.account.entity.Token
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TokenRepository: JpaRepository<Token, Long> {
    fun findByUserPhoneNumber(userPhoneNumber: String): Token?
}