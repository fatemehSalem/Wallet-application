package com.micro.account.service

import com.micro.account.entity.*
import com.micro.account.repository.AccountRepository
import com.micro.account.utils.PasswordUtils.encryptPassword
import com.micro.account.utils.PasswordUtils.verifyPassword
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*

@Service
class AccountService(

    private val accountRepository: AccountRepository,
    private val tokenRetriever: TokenRetriever,
    private val tokenService: TokenService,

) {
    val accountMap: HashMap<String, AccountDto> = HashMap()
    fun findByUserPhoneNumber(userPhoneNumber : String): Account? {
        return accountRepository.findByUserPhoneNumber(userPhoneNumber)
    }

    fun createNewAccountInMemory(accountRequest: AccountRequest):ResponseEntity<String> {

        var accountDto = AccountDto(
            accountRequest?.account_number ?: "",
            accountRequest.password,
            accountRequest.currency_code,
            accountRequest?.alias ?: "",
            accountRequest?.user_number ?: "",
            accountRequest.user_first_name,
            accountRequest.user_last_name,
            accountRequest.user_phone_country_code,
            accountRequest.user_phone_number,
            accountRequest?.user_email ?: ""


        )
      //  var userId = UUID.randomUUID().toString()
        accountMap["123"] = accountDto

         return generateOtp(accountRequest.user_phone_number)
    }


    fun createNewAccount( otpCheck: OTPCheck): ResponseEntity<String> {
        val accountDto = accountMap["123"]
        if (accountDto != null) {

            var accessToken = tokenRetriever.retrieveToken()

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

            val restTemplate = RestTemplate()
            val headers = HttpHeaders()
            headers.set("Authorization", "Bearer $accessToken")
            headers.contentType = MediaType.APPLICATION_JSON

            val requestEntity = HttpEntity(accountRequest, headers)

            val responseEntity = restTemplate.exchange(
                "https://stsapiuat.walletgate.io/v1/Account/RegisterPersonalAccount",
                HttpMethod.POST,
                requestEntity,
                AccountResponse::class.java
            )

            val accountResponse = responseEntity.body
            if (accountResponse != null) {
                val account = Account()
                account.accountNumber = accountRequest?.account_number ?: ""
                account.currencyCode = accountRequest.currency_code
                account.alias = accountRequest?.alias ?: ""
                account.userNumber = accountRequest?.user_number ?: ""
                account.userFirstName = accountRequest.user_first_name
                account.userLastName = accountRequest.user_last_name
                account.userPhoneCountryCode = accountRequest.user_phone_country_code
                account.userPhoneNumber = accountRequest.user_phone_number
                account.userEmail = accountRequest?.user_email ?: ""
                account.addressLine1 = accountRequest?.contact_address.address_line1 ?: ""
                account.addressLine2 = accountRequest?.contact_address.address_line2 ?: ""
                account.zipPostalCode = accountRequest?.contact_address.zip_postal_code ?: ""
                account.stateProvinceCode = accountRequest?.contact_address.state_province_code ?: ""
                account.password = accountDto.password
                accountRepository.save(account)

            }
            // account.password = encryptPassword(accountDto.password)


            /*   fun createNewAccount(token: String) {*/
            /*     val requestBody = AccountRequest

         webClient.post()
             .uri("https://stsapiuat.walletgate.io/v1/Account/RegisterPersonalAccount")
             .header("Authorization", "Bearer $token")
             .bodyValue(requestBody)
             .retrieve()
             .bodyToMono(String::class.java)
             .block()
     }*/
        }
        return ResponseEntity("create Account success", HttpStatus.OK)
    }


    fun authenticate(phoneNumberOrEmail: String , password:String): Boolean {
        var retVal = false
/*        val map: HashMap<String, Boolean> = HashMap()*/
        try {
            var  user = accountRepository.findByUserPhoneNumber(phoneNumberOrEmail)
            if(user!=null){
                if(user.userPhoneNumber == phoneNumberOrEmail){
                    if(user.password == password){
                        retVal = true
                    }
                }
            }
         //   retVal = user != null && user.userPhoneNumber == phoneNumberOrEmail && verifyPassword(password,user.password)
        }catch (e:Exception){
            println("- Error finding user")
        }
        return retVal
    }
    fun splitLastFourDigits(phoneNumber: String): String {
        val lastIndex = phoneNumber.length - 1
        return if (lastIndex >= 3) {
            phoneNumber.substring(lastIndex - 3)
        } else {
            phoneNumber // Return the whole string if it has less than 4 characters
        }
    }
    fun generateOtp(phoneNumber: String): ResponseEntity<String> {
        val code = splitLastFourDigits(phoneNumber)
        return ResponseEntity("otp CODE IS GENERATED:$code", HttpStatus.OK)
    }

//    fun generateOtp(request: GenerateOtpRequest) {
//         webClient.post()
//            .uri("http://otp-service/api/otp/generateOtp:8080")
//            .bodyValue(request)
//            .retrieve()
//            .bodyToMono(String::class.java)
//    }



}