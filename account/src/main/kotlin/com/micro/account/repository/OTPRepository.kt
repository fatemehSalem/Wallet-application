package com.micro.account.repository

import com.micro.account.entity.model.OTP
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OTPRepository : JpaRepository<OTP, Long> {
    fun findByUserPhoneNumber(userPhoneNumber : String): OTP?
    fun findByUserOtpCode(userOtpCode: String): OTP?
}