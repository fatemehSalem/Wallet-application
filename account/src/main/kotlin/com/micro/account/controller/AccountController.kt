package com.micro.account.controller

import com.micro.account.entity.*
import com.micro.account.service.AccountService
import com.micro.account.service.TokenRetriever
import com.micro.account.service.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.HttpStatus

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
        if (accountService.authenticate(loginRequest.userEmail, loginRequest.phoneNumber)) {
            // Authentication successful
            //generate Provider Auth Token
            tokenRetriever.retrieveToken(loginRequest.phoneNumber)
            return ResponseEntity.ok().body("Authentication/login was successful")
        } else {
            // Authentication failed
            val errorCode = ErrorCode.INVALID_PHONE_NUMBER
            val errorMessage = "Invalid username or phone number"
            val errorResponse = ErrorResponse(errorCode, errorMessage)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
        }
    }

    //After successful Authentication
    @PostMapping("/generateOtp")
    fun generateOtp(@RequestBody request: GenerateOtpRequest) {
        return accountService.generateOtp(request.phoneNumber , request.userEmail)
    }

    @PostMapping("/createNewAccount")
/*    fun createAccount(@RequestBody accountRequest: AccountRequest): Mono<ResponseEntity<AccountResponse>> {*/
    fun createAccount(@RequestBody accountRequest: AccountRequest): ResponseEntity<Any> {
        val accountResponse = tokenService.findByUserPhoneNumber(accountRequest.user_phone_number)?.accessToken?.let { accountService.createNewAccount(it, accountRequest) }
       // return accountResponseMono.map { accountResponse ->
        return ResponseEntity.status(HttpStatus.OK).body(accountResponse)
       // }
    }
}


