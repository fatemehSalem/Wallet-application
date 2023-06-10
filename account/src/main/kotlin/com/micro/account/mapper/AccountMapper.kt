package com.micro.account.mapper

import com.micro.account.entity.model.Account
import com.micro.account.entity.response.RegisterAccountResponse

object AccountMapper {
    fun mapAccountToAccountResponse(account: Account): RegisterAccountResponse {
        return RegisterAccountResponse(
                account.accountNumber,
                account.walletNumber,
                account.currencyCode,
                account.alias,
                account.userNumber,
                account.userFirstName,
                account.userLastName,
                account.userPhoneCountryCode,
                account.userPhoneNumber,
                account.userEmail,
                account.addressLine1,
                account.addressLine2,
                account.zipPostalCode,
                account.stateProvinceCode)
    }
}