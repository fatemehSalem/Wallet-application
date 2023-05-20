package com.micro.otp.repository

import com.micro.otp.entity.OtpUserCode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OtpRepository: JpaRepository<OtpUserCode, Long> {

    fun findByUserPhoneNumber(userPhoneNumber : String) : OtpUserCode
    fun findByUserEmail(userEmail : String) : OtpUserCode

}