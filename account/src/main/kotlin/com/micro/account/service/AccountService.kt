package com.micro.account.service

import com.micro.account.entity.*
import com.micro.account.repository.AccountRepository
import com.micro.account.repository.OTPRepository
import com.micro.account.utils.GeneralUtils.createCustomResponse
import com.micro.account.utils.GeneralUtils.splitLastFourDigits
import com.micro.account.utils.GeneralUtils.generateRandomString
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*

@Service
class AccountService(

    private val accountRepository: AccountRepository,
    private val tokenRetriever: TokenRetriever,
    private val otpRepository: OTPRepository,

) {
    val accountMap: HashMap<String, AccountDto> = HashMap()
    fun findByUserPhoneNumber(userPhoneNumber : String): Account? {
        return accountRepository.findByUserPhoneNumber(userPhoneNumber)
    }

    fun createNewAccountInMemory(accountRequest: AccountRequest):ResponseEntity<CustomResponse<Any>>  {
        var accountDto = AccountDto(
            generateRandomString(),
            accountRequest.password,
            "TRY",
            "",
            "",
            accountRequest.user_first_name,
            accountRequest.user_last_name,
            accountRequest.user_phone_country_code,
            accountRequest.user_phone_number,
            accountRequest?.user_email ?: "",
            splitLastFourDigits(accountRequest.user_phone_number)
        )
      //  var userId = UUID.randomUUID().toString()
        accountMap["123"] = accountDto

         return generateOtp(accountRequest.user_phone_number)
    }


    fun createNewAccount(otpCheck: OTPCheck):  ResponseEntity<CustomResponse<Any>> {
        val accountDto = accountMap["123"]
        val account = Account()
        if(accountDto != null ){
            if( accountDto.otpCode!=otpCheck.code || accountDto.userPhoneNumber!=otpCheck.phoneNumber ){
                val errorCode = ErrorCode.INVALID_OTP_CODE
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createCustomResponse("",
                    "create Account was unsuccessful: OTP code or Phone number is invalid" ,errorCode.code ))
            } else{
             //   if ( accountDto.userPhoneNumber==otpCheck.phoneNumber ) {
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

                    account.accountNumber = accountRequest?.account_number
                    account.currencyCode = accountRequest.currency_code
                    account.alias = accountRequest?.alias
                    account.userNumber = accountRequest?.user_number
                    account.userFirstName = accountRequest.user_first_name
                    account.userLastName = accountRequest.user_last_name
                    account.userPhoneCountryCode = accountRequest.user_phone_country_code
                    account.userPhoneNumber = accountRequest.user_phone_number
                    account.userEmail = accountRequest?.user_email
                    account.addressLine1 = accountRequest?.contact_address.address_line1
                    account.addressLine2 = accountRequest?.contact_address.address_line2
                    account.zipPostalCode = accountRequest?.contact_address.zip_postal_code
                    account.stateProvinceCode = accountRequest?.contact_address.state_province_code
                    account.password = accountDto.password
                    accountRepository.save(account)
                    try {
                        callAccountRegister(accountRequest)
                    }catch (e:Exception){
                        val errorCode = ErrorCode.CREATE_ACCOUNT_WAS_UNSUCCESSFUL
                        /* val errorMessage = "CREATE ACCOUNT WAS UNSUCCESSFUL"
                         val errorResponse = ErrorResponse(errorCode, errorMessage)*/
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createCustomResponse("",
                            "create Account was unsuccessful" ,errorCode.code ))
                        //return ResponseEntity("Something wrong in creating Account", HttpStatus.UNAUTHORIZED)
                    }
             /*   } else{
                    val errorCode = ErrorCode.ACCOUNT_EXIST_WITH_THIS_PHONE_NUMBER
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createCustomResponse("",
                        "create Account was unsuccessful: User exist with this phone number" ,errorCode.code))
                }*/

            }
            return ResponseEntity.status(HttpStatus.OK).
            body(createCustomResponse(account,
                "create Account was successful" ,"200" ))
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createCustomResponse("",
            "create Account was unsuccessful: Something Is Wrong" ,"401" ))
       // return ResponseEntity("create Account success", HttpStatus.OK)
    }

    fun callAccountRegister(accountRequest: AccountRequestToSave): Payload {
        var accessToken = tokenRetriever.retrieveToken()
        val restTemplate = RestTemplate()
        val requestUrl = "https://stsapiuat.walletgate.io/v1/Account/RegisterPersonalAccount"
        val headers = HttpHeaders()
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        val requestBody =  HttpEntity(accountRequest, headers)

        val response = restTemplate.exchange(requestUrl, HttpMethod.POST, requestBody, AccountResponse::class.java)

        val payload = response.body?.payload

        return payload ?: throw IllegalStateException("Payload not found in the response.")
    }
    fun authenticate(phoneNumberOrEmail: String , password:String): Boolean {
        var retVal = false
        try {
            var  user = accountRepository.findByUserPhoneNumber(phoneNumberOrEmail)
            if(user!=null){
                if(user.userPhoneNumber == phoneNumberOrEmail){
                    if(user.password == password){
                        retVal = true
                        val code = splitLastFourDigits(phoneNumberOrEmail)
                        var otp = OTP()
                        otp.userPhoneNumber = phoneNumberOrEmail
                        otp.userOtpCode = code
                        otpRepository.save(otp)
                    }
                }
            }
         //   retVal = user != null && user.userPhoneNumber == phoneNumberOrEmail && verifyPassword(password,user.password)
        }catch (e:Exception){
            println("- Error finding user")
        }
        return retVal
    }

    fun generateOtp(phoneNumber: String): ResponseEntity<CustomResponse<Any>> {
        return ResponseEntity.status(HttpStatus.OK).
        body(createCustomResponse(splitLastFourDigits(phoneNumber) ,
            "OTP code is generated successfully" ,"200" ))
    }


     fun verifyOTP(otpCheck: OTPCheck):ResponseEntity<CustomResponse<Any>>{

         var otp = otpRepository.findByUserPhoneNumber(otpCheck.phoneNumber)
         if(otp != null){
             if(otp.userOtpCode == otpCheck.code && otp.userPhoneNumber == otpCheck.phoneNumber){
                 return ResponseEntity.status(HttpStatus.OK).
                 body(createCustomResponse("",
                     "Account OTP verification was Successful" ,"200" ))
                 //return ResponseEntity("", HttpStatus.OK)
             }
         }
         val errorCode = ErrorCode.INVALID_OTP_CODE
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createCustomResponse("",
             "Account OTP verification was unsuccessful: OTP code or Phone number is invalid" ,errorCode.code ))
     }

    fun getAccountInfo(userPhoneNumber: String):ResponseEntity<CustomResponse<Any>>{
        var account = accountRepository.findByUserPhoneNumber(userPhoneNumber)
        if(account!= null){
            var accessToken = tokenRetriever.retrieveToken()
            val restTemplate = RestTemplate()
            val requestUrl = "https://stsapiuat.walletgate.io/v1/Account/GetPersonalAccount"
            val headers = HttpHeaders()
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            val requestBody =  HttpEntity(GetAccountInfoRequest(account.accountNumber), headers)

            val response = restTemplate.exchange(requestUrl, HttpMethod.POST, requestBody, GetAccountInfoResponse::class.java)
            val payload = response.body?.payload
            return ResponseEntity.status(HttpStatus.OK).
            body(createCustomResponse(payload?: "payload is null",
                "Get Account Info was successful" ,"200" ))
           // return  ResponseEntity(payload?: "payload is null", HttpStatus.OK)
        }
        val errorCode = ErrorCode.ACCOUNT_IS_NOT_FOUND
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createCustomResponse("",
            "Get Account Info was not successful: Account is not found!" ,errorCode.code ))
     //   return  ResponseEntity("Account is not found", HttpStatus.UNAUTHORIZED)
    }


}