package com.micro.transaction.entity.model

import java.time.Instant
import java.time.LocalDateTime

data class AccessToken(
    var accessToken: String,
    var accountNumber: String,
    var creationTime: Instant = Instant.now(),
    var expirationTime: Instant = Instant.now(),
    var createdAt: LocalDateTime = LocalDateTime.now()
)
