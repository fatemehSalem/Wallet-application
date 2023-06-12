package com.micro.account.service

import com.micro.account.entity.*
import com.micro.account.entity.dto.AccountDto
import com.micro.account.entity.model.*
import com.micro.account.entity.dto.P2PTransferRequestDto
import com.micro.account.entity.dto.TopUpCreditCardDto
import com.micro.account.entity.request.*
import com.micro.account.entity.response.AccountResponse
import com.micro.account.entity.response.CustomResponse
import com.micro.account.entity.response.P2PTransferResponse
import com.micro.account.mapper.AccountMapper
import com.micro.account.repository.AccountRepository
import com.micro.account.repository.OTPRepository
import com.micro.account.utils.GeneralUtils.generateRandomNumber
import com.micro.account.utils.GeneralUtils.splitLastFourDigits
import com.micro.account.utils.PasswordEncoderUtils
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
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
        /*  if(accountRepository.findByUserPhoneNumber(accountRequest.user_phone_number)!= null) {
            val errorCode = ErrorCode.ACCOUNT_EXIST_WITH_THIS_PHONE_NUMBER
            val response = CustomResponse(null,
                    "register Account was unsuccessful: account exist with this user phone number",
                    errorCode.code)
            return ResponseEntity.ok(response)
        } else{*/
        val accountDto = AccountDto(
            "",
            accountRequest.password,
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
            if (accountDto.otpCode != otpCheck.code || accountDto.userPhoneNumber != otpCheck.phoneNumber) {
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
        val restTemplate = RestTemplate()
        val requestUrl = "https://stsapiuat.walletgate.io/v1/Account/RegisterPersonalAccount"
        val headers = HttpHeaders()
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer $accessTokenToAPIs")
        val requestBody = HttpEntity(accountRequest, headers)

        val response = restTemplate.exchange(requestUrl, HttpMethod.POST, requestBody, AccountResponse::class.java)

        val payload = response.body?.payload
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
                        val otp = OTP()
                        otp.userPhoneNumber = userPhoneNumber
                        otp.userOtpCode = code
                        otpRepository.save(otp)
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
        val otp = otpRepository.findByUserPhoneNumber(otpCheck.phoneNumber)
        if (otp != null) {
            if (otp.userOtpCode == otpCheck.code && otp.userPhoneNumber == otpCheck.phoneNumber) {
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

    fun getAccountInfo(userPhoneNumber: String): ResponseEntity<Any> {
        val account = accountRepository.findByUserPhoneNumber(userPhoneNumber)
        val accessToken: String
        if (account != null) {
            if (accessTokenService.isAccessTokenExpired(account.accountNumber)){
                accessToken =   tokenRetriever.retrieveToken()
                accessTokenService.saveAccessToken(accessToken, account.accountNumber)
            }
            else
                accessToken =  accessTokenService.findByAccountNumber(account.accountNumber)?.accessToken
                    ?: tokenRetriever.retrieveToken()

            val restTemplate = RestTemplate()
            val requestUrl = "https://stsapiuat.walletgate.io/v1/Account/GetPersonalAccount"
            val headers = HttpHeaders()
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            val requestBody = HttpEntity(GetAccountInfoRequest(account.accountNumber), headers)

            val response =
                restTemplate.exchange(requestUrl, HttpMethod.POST, requestBody, GetAccountInfoResponse::class.java)
            val payload = response.body?.payload
            val response2 = CustomResponse(payload ?: "payload is null", "Get Account Info was successful")
            return ResponseEntity.ok(response2)
        }
        val errorCode = ErrorCode.ACCOUNT_IS_NOT_FOUND
        val response3 = CustomResponse(
            null,
            "Get Account Info was not successful: Account is not found!", errorCode.code
        )
        return ResponseEntity.ok(response3)
    }

    fun changeAccountPassword(request: ChangeAccountPasswordRequest): ResponseEntity<Any> {
        val account = accountRepository.findByUserPhoneNumber(request.phoneNumber)
        if (account != null) {
            account.password = PasswordEncoderUtils.encryptPassword(request.newPassword)
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

    fun personalToPersonalTransfer(request: P2PTransferRequestDto): ResponseEntity<Any> {
        val senderWalletNumber = accountRepository.findByAccountNumber(request.sender_account_number)?.walletNumber
        val receiverWalletNumber = accountRepository.findByAccountNumber(request.receiver_account_number)?.walletNumber
        var accessToken =""
        if (senderWalletNumber != null
            && receiverWalletNumber != null
        ) {
            val request = P2PTransferRequest(
                request.sender_account_number,
                senderWalletNumber,
                request.amount,
                "TRY",
                receiverWalletNumber,
                generateRandomNumber(),
                "",
                "",
                "",
                ""
            )
            if (accessTokenService.isAccessTokenExpired(request.sender_account_number)){
                accessToken =   tokenRetriever.retrieveToken()
                accessTokenService.saveAccessToken(accessToken, request.sender_account_number)
            }
            else
                accessToken =  accessTokenService.findByAccountNumber(request.sender_account_number)?.accessToken
                    ?: tokenRetriever.retrieveToken()
            val restTemplate = RestTemplate()
            val requestUrl = "https://stsapiuat.walletgate.io/v1/Transaction/PersonalToPersonalTransfer"
            val headers = HttpHeaders()
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            val requestBody = HttpEntity(request, headers)

            val response =
                restTemplate.exchange(requestUrl, HttpMethod.POST, requestBody, P2PTransferResponse::class.java)
            val payload = response.body?.payload
            var response2: CustomResponse<*>?
            response2 = if (payload != null) {

                CustomResponse(payload, "Personal To Personal Transfer was successful")
            } else {
                val errorCode = ErrorCode.PERSONAL_TO_PERSONAL_TRANSFER_WAS_UNSUCCESSFUL
                CustomResponse(
                    null, "Personal To Personal Transfer was unsuccessful",
                    errorCode.code
                )
            }
            return ResponseEntity.ok(response2)
        }
        val errorCode = ErrorCode.ACCOUNT_IS_NOT_FOUND
        return ResponseEntity.ok(
            CustomResponse(
                null,
                "Personal To Personal Transfer was unsuccessful: sender_account_number or receiver_account_number is wrong",
                errorCode.code
            )
        )

    }

    fun topUpCreditCard(request: TopUpCreditCardDto): ResponseEntity<Any> {
        val senderWalletNumber = accountRepository.findByAccountNumber(request.sender_account_number)?.walletNumber
        var accessToken =""
        if (senderWalletNumber != null) {
            if (accessTokenService.isAccessTokenExpired(request.sender_account_number)){
                accessToken =   tokenRetriever.retrieveToken()

                accessTokenService.saveAccessToken(accessToken, request.sender_account_number)
            }
            else
                accessToken =  accessTokenService.findByAccountNumber(request.sender_account_number)?.accessToken
                    ?: tokenRetriever.retrieveToken()
            val topUpRequest = TopUpCreditCardRequest(
                "TRY",
                request.sender_account_number,
                "sipay_123",
                generateRandomNumber(),
                senderWalletNumber,
                request.amount,
                "",
        "",
                ""
            )
            val restTemplate = RestTemplate()
            val requestUrl = "https://stsapiuat.walletgate.io/v1/Transaction/TopupCreditCard"
            val headers = HttpHeaders()
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            val requestBody = HttpEntity(topUpRequest, headers)
            val response =
                restTemplate.exchange(requestUrl, HttpMethod.POST, requestBody, P2PTransferResponse::class.java)
            val payload = response.body?.payload
            return ResponseEntity.ok(CustomResponse(payload, "TopUp Credit Card was successful"))
        }
        val errorCode = ErrorCode.ACCOUNT_IS_NOT_FOUND
        return ResponseEntity.ok(
            CustomResponse(
                null,
                "TopUp Credit Card was unsuccessful: sender_account_number is wrong",
                errorCode.code
            )
        )


    }
}