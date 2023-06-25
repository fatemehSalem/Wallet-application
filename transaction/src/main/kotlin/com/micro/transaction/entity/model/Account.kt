package com.micro.transaction.entity.model

import java.time.LocalDateTime

data class Account(
    var accountNumber: String,
    var walletNumber: String,
    var walletId: String,
    var password: String,
    var currencyCode: String,
    var alias: String,
    var userNumber: String,
    var userFirstName: String,
    var userLastName: String,
    var userPhoneCountryCode: String,
    var userPhoneNumber: String,
    var userEmail: String,
    var addressLine1: String,
    var addressLine2: String,
    var zipPostalCode: String,
    var stateProvinceCode: String,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
