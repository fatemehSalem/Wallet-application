package com.micro.account.service

import com.micro.account.entity.Account
import com.micro.account.entity.AccountRequest
import com.micro.account.entity.AccountResponse
import com.micro.account.repository.AccountRepository
import com.micro.account.utils.PasswordUtils.encryptPassword
import com.micro.account.utils.PasswordUtils.verifyPassword
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class AccountService(private val webClient: WebClient,
                     @Value("\${api.base-uri}")
                     private val baseUri: String,
                     private val accountRepository: AccountRepository
) {

    fun findByUserPhoneNumber(userPhoneNumber : String): Account? {
        return accountRepository.findByUserPhoneNumber(userPhoneNumber)
    }

    fun createNewAccount(access_token: String, accountRequest: AccountRequest){

        val responseEntity: ResponseEntity<AccountResponse>? = webClient.post()
            .uri("$baseUri/RegisterPersonalAccount")
            .header("Authorization", "Bearer $access_token")
            .bodyValue(accountRequest)
            .retrieve()
            .toEntity(AccountResponse::class.java)
            .block()

        val accountResponse = responseEntity?.body
        if (accountResponse != null) {
            val account = Account()
            account.accountNumber = accountRequest.account_number ?: ""
            account.currencyCode = accountRequest.currency_code
            account.alias = accountRequest.alias ?: ""
            account.userNumber = accountRequest.user_number ?: ""
            account.userFirstName = accountRequest.user_first_name
            account.userLastName = accountRequest.user_last_name
            account.userPhoneCountryCode = accountRequest.user_phone_country_code
            account.userPhoneNumber = accountRequest.user_phone_number
            account.userEmail = accountRequest.user_email ?: ""
            account.addressLine1 = accountRequest.contact_address.address_line1 ?: ""
            account.addressLine2 = accountRequest.contact_address.address_line2 ?: ""
            account.zipPostalCode = accountRequest.contact_address.zip_postal_code ?: ""
            account.stateProvinceCode = accountRequest.contact_address.state_province_code ?: ""
            account.password = encryptPassword(accountRequest.password)
            accountRepository.save(account)
        }
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


    fun authenticate(phoneNumberOrEmail: String , password:String): Boolean {
        var retVal = false
        if(phoneNumberOrEmail != null){
            var  user = accountRepository.findByUserPhoneNumber(phoneNumberOrEmail)!!
            retVal = user != null && user.userPhoneNumber == phoneNumberOrEmail && verifyPassword(password,user.password)
        }
        return retVal
    }

    fun generateOtp(request: String) {
        val responseMono = webClient.post()
            .uri("http://otp-service/api/otp/generateOtp:8080")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(String::class.java)

    }

}