package com.micro.account.service

import com.micro.account.entity.dto.AccountDto
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class RedisService(private val redisTemplate: RedisTemplate<String, Any>) {

//    ------------------- OTP -------------------
    fun saveOtpDataInRedis(userOtpCode: String, userPhoneNumber: String, otpCreatedTime: Long): String {
        val uuid = UUID.randomUUID().toString()
        val data = mapOf(
            "userOtpCode" to userOtpCode,
            "userPhoneNumber" to userPhoneNumber,
            "otpCreatedTime" to otpCreatedTime
        )
        redisTemplate.opsForHash<String, Any>().putAll(uuid, data)
        return uuid
    }

    fun getDataInRedis(uuid: String): Map<String, Any>? {
        return redisTemplate.opsForHash<String, Any>().entries(uuid)
    }

    fun dataIsExpiredInRedis(uuid: String): Boolean {
        val currentTime = System.currentTimeMillis()
        val savedData = getDataInRedis(uuid)
        val otpCreatedTime = savedData?.get("otpCreatedTime") as? Long

        if (otpCreatedTime != null && currentTime - otpCreatedTime > 120000) {
           // redisTemplate.delete(uuid)
            return true
        }

        return false //return UUID
    }

//    ------------------- Account Information -------------------
    fun saveAccountDataInRedis(accountDto: AccountDto): String {
        val uuid = UUID.randomUUID().toString()
        val data = mapOf(
            "accountNumber" to accountDto.accountNumber,
            "password" to accountDto.password,
            "currencyCode" to accountDto.currencyCode,
            "alias" to accountDto.alias,
            "userNumber" to accountDto.userNumber,
            "userFirstName" to accountDto.userFirstName,
            "userLastName" to accountDto.userLastName,
            "userPhoneCountryCode" to accountDto.userPhoneCountryCode,
            "userPhoneNumber" to accountDto.userPhoneNumber,
            "userEmail" to accountDto.userEmail,
            "otpCode" to accountDto.otpCode,
            "otpCreatedTime" to System.currentTimeMillis()

        )
        redisTemplate.opsForHash<String, Any>().putAll(uuid, data)
        return uuid
    }
}
