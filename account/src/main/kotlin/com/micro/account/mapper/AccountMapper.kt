package com.micro.account.mapper

import com.micro.account.entity.model.Account
import com.micro.account.entity.response.KafkaAccountResponse
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

    fun mapAccountToKafkaAccountResponse(account: Account): KafkaAccountResponse {
        return KafkaAccountResponse(
            account.accountNumber,
            account.walletNumber,
            account.walletId,
            account.userFirstName,
            account.userLastName,
            account.userPhoneNumber,
            account.userEmail
        )
    }

        //fun mapResponseToAccount()
}