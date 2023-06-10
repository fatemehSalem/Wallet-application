package com.micro.account.repository

import com.micro.account.entity.model.AccessToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccessTokenRepository: JpaRepository<AccessToken, Long> {
    fun findByUserPhoneNumber(userPhoneNumber: String): AccessToken?
}