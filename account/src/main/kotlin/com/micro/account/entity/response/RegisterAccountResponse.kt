package com.micro.account.entity.response


data class RegisterAccountResponse (
        var accountNumber: String,
        var walletNumber: String,
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
        var stateProvinceCode: String
)