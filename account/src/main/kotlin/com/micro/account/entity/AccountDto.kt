package com.micro.account.entity

data class AccountDto (

    var accountNumber: String,

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

    var stateProvinceCode: String
)