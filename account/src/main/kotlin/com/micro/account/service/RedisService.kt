package com.micro.account.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class RedisService(private val redisTemplate: RedisTemplate<String, Any>) {

    fun saveData(userOtpCode: String, userPhoneNumber: String, otpCreatedTime: Long): String {
        val uuid = UUID.randomUUID().toString()
        val data = mapOf(
            "userOtpCode" to userOtpCode,
            "userPhoneNumber" to userPhoneNumber,
            "otpCreatedTime" to otpCreatedTime
        )
        redisTemplate.opsForHash<String, Any>().putAll(uuid, data)
        return uuid
    }

    fun getData(uuid: String): Map<String, Any>? {
        return redisTemplate.opsForHash<String, Any>().entries(uuid)
    }

/*    fun getDataByPhoneNumber(userPhoneNumber: String): Map<String, Any>? {
        val keys = redisTemplate.keys("*") // Get all keys in the Redis database
        for (key in keys) {
            val savedData = getData(key)
            val phoneNumber = savedData?.get("userPhoneNumber") as? String
            if (phoneNumber == userPhoneNumber) {
                return savedData
            }
        }
        return null // Data not found for the specified phone number
    }*/

    fun dataIsExpired(uuid: String): Boolean {
        val currentTime = System.currentTimeMillis()
        val savedData = getData(uuid)
        val otpCreatedTime = savedData?.get("otpCreatedTime") as? Long

        if (otpCreatedTime != null && currentTime - otpCreatedTime > 120000) {
            redisTemplate.delete(uuid)
            return true
        }

        return false //return UUID
    }
}
