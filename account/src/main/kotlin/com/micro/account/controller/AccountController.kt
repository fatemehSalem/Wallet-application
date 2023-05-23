package com.micro.account.controller

import com.micro.account.entity.*
import com.micro.account.service.AccountService
import com.micro.account.service.TokenRetriever
import com.micro.account.service.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/account")
class AccountController {

    @Autowired
    private lateinit var tokenService: TokenService

    @Autowired
    private lateinit var accountService: AccountService

    @Autowired
    private lateinit var tokenRetriever: TokenRetriever



    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Any> {
        return if (accountService.authenticate(loginRequest.phoneNumberOrEmail ,loginRequest.password )) {
            accountService.generateOtp(loginRequest.phoneNumberOrEmail)
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
        return accountService.generateOtp(request.phoneNumber)
    }

    @PostMapping("/createNewAccount")
/*    fun createAccount(@RequestBody accountRequest: AccountRequest): Mono<ResponseEntity<AccountResponse>> {*/
    fun createAccount(@RequestBody accountRequest: AccountRequest): ResponseEntity<Any> {
        val accountResponse = tokenService.findByUserPhoneNumber(accountRequest.user_phone_number)?.accessToken?.let { accountService.createNewAccount(it, accountRequest) }
       // return accountResponseMono.map { accountResponse ->
        return ResponseEntity.status(HttpStatus.OK).body(accountResponse)
       // }
    }

    @GetMapping("/findByUserPhoneNumber/{phoneNumber}")
        fun findByUserPhoneNumber(@PathVariable phoneNumber: String): LoginRequest? {
        var account = accountService.findByUserPhoneNumber(phoneNumber)
        var loginRequest = LoginRequest("" , "")
        if (account != null) {
             loginRequest = LoginRequest(password = account.password, phoneNumberOrEmail = account.userPhoneNumber)
        }
        return loginRequest
    }

}


