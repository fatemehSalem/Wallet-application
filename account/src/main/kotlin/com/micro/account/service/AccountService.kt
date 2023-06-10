package com.micro.account.service

import com.micro.account.entity.*
import com.micro.account.entity.dto.AccountDto
import com.micro.account.entity.model.*
import com.micro.account.entity.request.AccountRequest
import com.micro.account.entity.request.ChangeAccountPasswordRequest
import com.micro.account.entity.request.GetAccountInfoRequest
import com.micro.account.entity.response.AccountResponse
import com.micro.account.entity.response.CustomResponse
import com.micro.account.repository.AccountRepository
import com.micro.account.repository.OTPRepository
import com.micro.account.utils.GeneralUtils.splitLastFourDigits
import com.micro.account.utils.PasswordEncoderUtils
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*

@Service
class AccountService(

    private val accountRepository: AccountRepository,
    private val tokenRetriever: TokenRetriever,
    private val accessTokenService: AccessTokenService,
    private val otpRepository: OTPRepository,
    ) {
    val accountMap: HashMap<String, AccountDto> = HashMap()
    fun findByUserPhoneNumber(userPhoneNumber : String): Account? {
        return accountRepository.findByUserPhoneNumber(userPhoneNumber)
    }

    fun createNewAccountInMemory(accountRequest: AccountRequest): ResponseEntity<Any>  {
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
    }

    fun createNewAccount(otpCheck: OTPCheck):  ResponseEntity<Any> {
        val accountDto = accountMap["123"]
        val account = Account()
        if(accountDto != null ){
            if( accountDto.otpCode!=otpCheck.code || accountDto.userPhoneNumber!=otpCheck.phoneNumber ){
                val errorCode = ErrorCode.INVALID_OTP_CODE
                val response = CustomResponse("",
                    "create Account was unsuccessful: OTP code or Phone number is invalid", errorCode.code)
                return ResponseEntity.ok(response)
            } else{
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
                   // account.accountNumber = accountRequest.account_number
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
                }catch (e:Exception){
                    val errorCode = ErrorCode.CREATE_ACCOUNT_WAS_UNSUCCESSFUL
                    /* val errorMessage = "CREATE ACCOUNT WAS UNSUCCESSFUL"
                     val errorResponse = ErrorResponse(errorCode, errorMessage)*/
                    val response = CustomResponse("", "create Account was unsuccessful", errorCode.code)
                    return ResponseEntity.ok(response)
                }
            }

            val response = CustomResponse(account, "create Account was Successful")
            return ResponseEntity.ok(response)
        }
        val response = CustomResponse("", "create Account was unsuccessful: Something Is Wrong",401)
        return ResponseEntity.ok(response)
    }

    fun callAccountRegister(accountRequest: AccountRequestToSave): Payload {
        val accessTokenToAPIs = tokenRetriever.retrieveToken()
        accessTokenService.saveAccessToken(accessTokenToAPIs,accountRequest.user_phone_number) //save accesstoken for using in next call APIS
        val restTemplate = RestTemplate()
        val requestUrl = "https://stsapiuat.walletgate.io/v1/Account/RegisterPersonalAccount"
        val headers = HttpHeaders()
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer $accessTokenToAPIs")
        val requestBody =  HttpEntity(accountRequest, headers)

        val response = restTemplate.exchange(requestUrl, HttpMethod.POST, requestBody, AccountResponse::class.java)

        val payload = response.body?.payload

        return payload ?: throw IllegalStateException("Payload not found in the response.")
    }

    fun authenticate(userPhoneNumber: String , password:String): Boolean {
        var retVal = false
        try {
            val user = accountRepository.findByUserPhoneNumber(userPhoneNumber)
            if(user!=null){
                if(user.userPhoneNumber == userPhoneNumber){
                 //  if(user.password == password)
                if(PasswordEncoderUtils.verifyPassword(password,user.password))
                    {
                        retVal = true
                        val code = splitLastFourDigits(userPhoneNumber)
                        val otp = OTP()
                        otp.userPhoneNumber = userPhoneNumber
                        otp.userOtpCode = code
                        otpRepository.save(otp)
                    }
                }
            }
        }catch (e:Exception){
            println("- Error finding user")
        }
        return retVal
    }

    fun generateOtp(phoneNumber: String): ResponseEntity<Any> {
        val response = CustomResponse("",
            "OTP code is generated successfully")
        return ResponseEntity.ok(response)
    }


     fun verifyOTP(otpCheck: OTPCheck):ResponseEntity<Any>{
         val otp = otpRepository.findByUserPhoneNumber(otpCheck.phoneNumber)
         if(otp != null){
             if(otp.userOtpCode == otpCheck.code && otp.userPhoneNumber == otpCheck.phoneNumber){
                 val response = CustomResponse("", "Account OTP verification was Successful")
                 return ResponseEntity.ok(response)
             }
         }
         val errorCode = ErrorCode.INVALID_OTP_CODE
         val response = CustomResponse("",
             "Account OTP verification was unsuccessful: OTP code or Phone number is invalid",errorCode.code)
         return ResponseEntity.ok(response)
     }

    fun getAccountInfo(userPhoneNumber: String):ResponseEntity<Any>{
        val account = accountRepository.findByUserPhoneNumber(userPhoneNumber)
        val accessToken: String
        if(account!= null){
            accessToken = if(accessTokenService.isAccessTokenExpired(userPhoneNumber))
                tokenRetriever.retrieveToken()
            else
                accessTokenService.findByUserPhoneNumber(userPhoneNumber)?.accessToken ?:tokenRetriever.retrieveToken()

            val restTemplate = RestTemplate()
            val requestUrl = "https://stsapiuat.walletgate.io/v1/Account/GetPersonalAccount"
            val headers = HttpHeaders()
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            val requestBody =  HttpEntity(GetAccountInfoRequest(account.accountNumber), headers)

            val response = restTemplate.exchange(requestUrl, HttpMethod.POST, requestBody, GetAccountInfoResponse::class.java)
            val payload = response.body?.payload
            val response2 = CustomResponse(payload?: "payload is null", "Get Account Info was successful")
            return ResponseEntity.ok(response2)
        }
        val errorCode = ErrorCode.ACCOUNT_IS_NOT_FOUND
        val response3 = CustomResponse("",
            "Get Account Info was not successful: Account is not found!",errorCode.code)
        return ResponseEntity.ok(response3)
    }

    fun changeAccountPassword(request: ChangeAccountPasswordRequest):ResponseEntity<Any>{
        val account = accountRepository.findByUserPhoneNumber(request.phoneNumber)
        if(account!= null){
            account.password = PasswordEncoderUtils.encryptPassword(request.newPassword)
            accountRepository.save(account)
            val response = CustomResponse(account, "Change Account Password was Successful")
            return ResponseEntity.ok(response)
        }
        val errorCode = ErrorCode.ACCOUNT_CHANGE_PASSWORD_WAS_UNSUCCESSFUL
        val response = CustomResponse("",
            "Change Account Password was not successful: Account is not found!",errorCode.code)
        return ResponseEntity.ok(response)
    }



}