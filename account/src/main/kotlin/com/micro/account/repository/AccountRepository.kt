package com.micro.account.repository

import com.micro.account.entity.model.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface  AccountRepository: JpaRepository<Account, Long> {
    fun findByUserEmail(userEmail: String): Account?
    fun findByUserPhoneNumber(userPhoneNumber : String): Account?
    fun findByAccountNumber(accountNumber : String): Account?
}