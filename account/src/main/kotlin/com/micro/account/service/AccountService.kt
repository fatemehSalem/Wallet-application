package com.micro.account.service

import com.micro.account.entity.*
import com.micro.account.entity.dto.*
import com.micro.account.entity.model.*
import com.micro.account.entity.request.*
import com.micro.account.entity.response.CustomResponse
import com.micro.account.mapper.AccountMapper
import com.micro.account.repository.AccountRepository
import com.micro.account.repository.OTPRepository
import com.micro.account.utils.GeneralUtils
import com.micro.account.utils.GeneralUtils.splitLastFourDigits
import com.micro.account.utils.PasswordEncoderUtils
import org.springframework.http.*
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class AccountService(

    private val accountRepository: AccountRepository,
    private val tokenRetriever: TokenRetrieverService,
    private val accessTokenService: AccessTokenService,
    private val otpRepository: OTPRepository,
) {
    val accountMap: HashMap<String, AccountDto> = HashMap()
    fun findByUserPhoneNumber(userPhoneNumber: String): Account? {
        return accountRepository.findByUserPhoneNumber(userPhoneNumber)
    }

    fun createNewAccountInMemory(accountRequest: AccountRequest): ResponseEntity<Any> {
        val accountDto = AccountDto(
            "",
            accountRequest.user_password,
            "TRY",
            "",
            "",
            accountRequest.user_first_name,
            accountRequest.user_last_name,
            accountRequest.user_phone_country_code,
            accountRequest.user_phone_number,
            accountRequest.user_email,
            splitLastFourDigits(accountRequest.user_phone_number)
        )
        accountMap["123"] = accountDto

        return generateOtp(accountRequest.user_phone_number)
        //}
    }

    fun createNewAccount(otpCheck: OTPCheck): ResponseEntity<Any> {
        val accountDto = accountMap["123"]
        val account = Account()
        if (accountDto != null) {
            if (accountDto.otpCode != otpCheck.user_otp_code || accountDto.userPhoneNumber != otpCheck.user_phone_number) {
                val errorCode = ErrorCode.INVALID_OTP_CODE
                val response = CustomResponse(
                    null,
                    "create Account was unsuccessful: OTP code or Phone number is invalid",
                    errorCode.code
                )
                return ResponseEntity.ok(response)
            } else {
                try {
                    val contactAddress = ContactAddress(
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                    )

                    val accountRequest = AccountRequestToSave(
                        accountDto.accountNumber,
                        accountDto.currencyCode,
                        accountDto.alias,
                        accountDto.userNumber,
                        accountDto.userFirstName,
                        accountDto.userLastName,
                        accountDto.userPhoneCountryCode,
                        accountDto.userPhoneNumber,
                        accountDto.userEmail,
                        contactAddress
                    )
                    val payload = callAccountRegister(accountRequest)

                    account.accountNumber = payload.account_number.toString()
                    account.walletNumber = payload.wallet_number.toString()
                    account.currencyCode = accountRequest.currency_code
                    account.alias = accountRequest.alias
                    account.userNumber = accountRequest.user_number
                    account.userFirstName = accountRequest.user_first_name
                    account.userLastName = accountRequest.user_last_name
                    account.userPhoneCountryCode = accountRequest.user_phone_country_code
                    account.userPhoneNumber = accountRequest.user_phone_number
                    account.userEmail = accountRequest.user_email
                    account.addressLine1 = accountRequest.contact_address.address_line1
                    account.addressLine2 = accountRequest.contact_address.address_line2
                    account.zipPostalCode = accountRequest.contact_address.zip_postal_code
                    account.stateProvinceCode = accountRequest.contact_address.state_province_code
                    account.password = PasswordEncoderUtils.encryptPassword(accountDto.password)
                    account.createdAt = LocalDateTime.now()
                    account.updatedAt = LocalDateTime.now()
                    accountRepository.save(account)

                } catch (e: Exception) {
                    val errorCode = ErrorCode.CREATE_ACCOUNT_WAS_UNSUCCESSFUL
                    val response = CustomResponse(null, "create Account was unsuccessful", errorCode.code)
                    return ResponseEntity.ok(response)
                }
            }
            val response =
                CustomResponse(AccountMapper.mapAccountToAccountResponse(account), "create Account was Successful")
            return ResponseEntity.ok(response)
        }
        val response = CustomResponse("", "create Account was unsuccessful: Something Is Wrong", 401)
        return ResponseEntity.ok(response)
    }

    fun callAccountRegister(accountRequest: AccountRequestToSave): Payload {
        val accessTokenToAPIs = tokenRetriever.retrieveToken()
        val payload = GeneralUtils.runBackOfficeApI(
            "https://stsapiuat.walletgate.io/v1/Account/RegisterPersonalAccount",
            accessTokenToAPIs, accountRequest
        )
        if (payload != null) {
            //save accessToken for using in next call APIS
            accessTokenService.saveAccessToken(accessTokenToAPIs, payload.account_number.toString())
        }
        return payload ?: throw IllegalStateException("Payload not found in the response.")
    }

    fun authenticate(userPhoneNumber: String, password: String): Boolean {
        var retVal = false
        try {
            val user = accountRepository.findByUserPhoneNumber(userPhoneNumber)
            if (user != null) {
                if (user.userPhoneNumber == userPhoneNumber) {
                    if (PasswordEncoderUtils.verifyPassword(password, user.password)) {
                        retVal = true
                        val code = splitLastFourDigits(userPhoneNumber)
                        if (otpRepository.findByUserPhoneNumber(userPhoneNumber) == null) {
                            val otpToSave = OTP()
                            otpToSave.userPhoneNumber = userPhoneNumber
                            otpToSave.userOtpCode = code
                            otpToSave.createdAt = LocalDateTime.now()
                            otpRepository.save(otpToSave)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("- Error finding user")
        }
        return retVal
    }

    fun generateOtp(phoneNumber: String): ResponseEntity<Any> {

        val response = CustomResponse(
            null,
            "OTP code is generated successfully"
        )
        return ResponseEntity.ok(response)
    }


    fun verifyOTP(otpCheck: OTPCheck): ResponseEntity<Any> {
        val otp = otpRepository.findByUserPhoneNumber(otpCheck.user_phone_number)
        if (otp != null) {
            if (otp.userOtpCode == otpCheck.user_otp_code && otp.userPhoneNumber == otpCheck.user_phone_number) {
                val response = CustomResponse(null, "Account OTP verification was Successful")
                return ResponseEntity.ok(response)
            }
        }
        val errorCode = ErrorCode.INVALID_OTP_CODE
        val response = CustomResponse(
            null,
            "Account OTP verification was unsuccessful: OTP code or Phone number is invalid", errorCode.code
        )
        return ResponseEntity.ok(response)
    }

    fun getAccountInfo(accountNumber: String): ResponseEntity<Any> {
        val account = accountRepository.findByAccountNumber(accountNumber)
        val accessToken: String
        if (account != null) {
            if (accessTokenService.isAccessTokenExpired(accountNumber)) {
                accessToken = tokenRetriever.retrieveToken()
                accessTokenService.saveAccessToken(accessToken, accountNumber)
            } else
                accessToken = accessTokenService.findByAccountNumber(accountNumber)?.accessToken
                    ?: tokenRetriever.retrieveToken()
            return getWalletIdByWalletInfo(account, accessToken)
        }
        val errorCode = ErrorCode.ACCOUNT_IS_NOT_FOUND
        val response3 = CustomResponse(
            null,
            "Get Account Info was not successful: Account is not found!", errorCode.code
        )
        return ResponseEntity.ok(response3)
    }

    fun getWalletIdByWalletInfo(account: Account, accessToken: String): ResponseEntity<Any> {
        val firstPayload = GeneralUtils.runBackOfficeApI(
            "https://stsapiuat.walletgate.io/v1/Account/GetPersonalAccount",
            accessToken, GetAccountInfoRequest(account.accountNumber)
        )
        if (firstPayload != null) {
            val secondPayload = GeneralUtils.runBackOfficeApI(
                "https://stsapiuat.walletgate.io/v1/wallet/info",
                accessToken, WalletInfoRequest(account.accountNumber, account.walletNumber)
            )
            if (secondPayload != null) {
                val getInfoResponse = GetAccountInfoResponseDto(
                    account.accountNumber,
                    secondPayload.wallet_info?.user_kyc_info?.first_name.toString(),
                    secondPayload.wallet_info?.user_kyc_info?.last_name.toString(),
                    secondPayload.wallet_info?.total_balance,
                    secondPayload.wallet_info?.transaction_limits?.max_balance,
                    secondPayload.wallet_info?.user_kyc_info?.kyc_level_status.toString(),
                    secondPayload.wallet_info?.user_kyc_info?.kyc_level.toString()
                )
                account.walletId = secondPayload.wallet_info?.id.toString()
                account.updatedAt = LocalDateTime.now()
                accountRepository.save(account)
                val response2 = CustomResponse(getInfoResponse, "Get Account Info was successful")
                return ResponseEntity.ok(response2)
            }
        }
        val errorCode = ErrorCode.ACCOUNT_GET_INFO_WAS_UNSUCCESSFUL
        val response3 = CustomResponse(
            null,
            "Get Account Info was not successful: Account is not found!", errorCode.code
        )
        return ResponseEntity.ok(response3)
    }

    fun changeAccountPassword(request: ChangeAccountPasswordRequest): ResponseEntity<Any> {
        val account = accountRepository.findByUserPhoneNumber(request.user_phone_number)
        if (account != null) {
            account.password = PasswordEncoderUtils.encryptPassword(request.user_new_password)
            account.updatedAt = LocalDateTime.now()
            accountRepository.save(account)
            val response = CustomResponse(account, "Change Account Password was Successful")
            return ResponseEntity.ok(response)
        }
        val errorCode = ErrorCode.ACCOUNT_CHANGE_PASSWORD_WAS_UNSUCCESSFUL
        val response = CustomResponse(
            null,
            "Change Account Password was not successful: Account is not found!", errorCode.code
        )
        return ResponseEntity.ok(response)
    }


}