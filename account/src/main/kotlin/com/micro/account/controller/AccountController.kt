package com.micro.account.controller

import com.micro.account.entity.*
import com.micro.account.service.AccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/account")
class AccountController {

    @Autowired
    private lateinit var accountService: AccountService


    @GetMapping("/testAccountService/{num}")
    fun test(@PathVariable num: String):String{
        return "testAccountService is ran!!"
    }
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest) {
         if (accountService.authenticate(loginRequest.phoneNumberOrEmail ,loginRequest.password )) {
             val request = GenerateOtpRequest(loginRequest.phoneNumberOrEmail , "0","123")
             accountService.generateOtp(request)
             ResponseEntity(accountService.findByUserPhoneNumber(loginRequest.phoneNumberOrEmail )?.id, HttpStatus.OK)
        } else {
            // Authentication failed
            val errorCode = ErrorCode.INVALID_PHONE_NUMBER
            val errorMessage = "Invalid phone number or password"
            val errorResponse = ErrorResponse(errorCode, errorMessage)
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
        }
    }

    //After successful Authentication
    @PostMapping("/generateOtp")
    fun generateOtp(@RequestBody request: GenerateOtpRequest) {
        return accountService.generateOtp(request)
    }

    @PostMapping("/createNewAccountBeforeOTP")
  fun createAccountBeforeOTP(@RequestBody accountRequest: AccountRequest){
            //1_Account info stored in Memory and OTP code generated
            accountService.createNewAccountInMemory(accountRequest)
             ResponseEntity.status(HttpStatus.OK)
            // }

    }
/*
    @PostMapping("/createNewAccountAfterOTP")
    {
         val accountResponse =
                tokenService.findByUserPhoneNumber(accountRequest.user_phone_number)?.accessToken?.let {
                    accountService.createNewAccount(
                        it,
                        accountRequest
                    )
                }
            // return accountResponseMono.map { accountResponse ->
            return ResponseEntity.status(HttpStatus.OK).body(accountResponse)
    }
*/

    @GetMapping("/findByUserPhoneNumber/{phoneNumber}")
        fun findByUserPhoneNumber(@PathVariable phoneNumber: String): LoginRequest? {
        val account = accountService.findByUserPhoneNumber(phoneNumber)
        var loginRequest = LoginRequest("" , "")
        if (account != null) {
             loginRequest = LoginRequest(account.password, account.userPhoneNumber)
        }
        return loginRequest
    }

}


