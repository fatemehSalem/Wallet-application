package com.micro.account.controller

import com.micro.account.entity.ErrorCode
import com.micro.account.entity.model.AccessToken
import com.micro.account.entity.model.Account
import com.micro.account.entity.model.OTPCheck
import com.micro.account.entity.request.AccountRequest
import com.micro.account.entity.request.ChangeAccountPasswordRequest
import com.micro.account.entity.request.LoginRequest
import com.micro.account.entity.response.CustomResponse
import com.micro.account.entity.response.LoginResponse
import com.micro.account.service.AccessTokenService
import com.micro.account.service.AccountService
import com.micro.account.service.TokenRetrieverService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import springfox.documentation.annotations.ApiIgnore

@RestController
@RequestMapping("/api/account")
@Api(value = "Account API", description = "Account Service APIs")
class AccountController {

    @Autowired
    private lateinit var accountService: AccountService
    @Autowired
    private lateinit var tokenRetrieverService: TokenRetrieverService
    @Autowired
    private lateinit var accessTokenService: AccessTokenService


    @PostMapping("/login")
    @ApiOperation("Account Login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Any> {
        if (accountService.authenticate(loginRequest.user_phone_number, loginRequest.user_password)) {
            accountService.generateOtp(loginRequest.user_phone_number)
            val response = accountService.findByUserPhoneNumber(loginRequest.user_phone_number)
                ?.let { CustomResponse(LoginResponse(it.accountNumber), "Account Authentication was Successful") }
            return ResponseEntity.ok(response)
        }
        val errorCode = ErrorCode.ACCOUNT_AUTHENTICATION_WAS_UNSUCCESSFUL
        val response = CustomResponse(null, "Account Authentication was unsuccessful", errorCode.code)
        return ResponseEntity.ok(response)

    }

    @PostMapping("/register")
    @ApiOperation("Register New Account")
    fun createAccountBeforeOTP(@RequestBody accountRequest: AccountRequest): ResponseEntity<Any> {
        //1_Account info stored in Memory and OTP code generated
        return accountService.createNewAccountInMemory(accountRequest)
        // }
    }

    @PostMapping("/signup")
    @ApiOperation("Account SignUp")
    fun createAccountBeforeOTP(@RequestBody otpCheck: OTPCheck): ResponseEntity<Any> {
        return accountService.createNewAccount(otpCheck)
    }

    @PostMapping("/authenticate")
    @ApiOperation("Account authenticate")
    fun login(@RequestBody otpCheck: OTPCheck): ResponseEntity<Any> {
        return accountService.verifyOTP(otpCheck)
    }

    @GetMapping("/getAccountInfo/{accountNumber}")
    @ApiOperation("Account get Info By Account_Number")
    fun getAccountInfo(@PathVariable accountNumber: String): ResponseEntity<Any> {
        return accountService.getAccountInfo(accountNumber)
    }

    @PostMapping("/changePassword")
    @ApiOperation("Account change Password")
    fun changeAccountPassword(@RequestBody request: ChangeAccountPasswordRequest): ResponseEntity<Any> {
        return accountService.changeAccountPassword(request)
    }


    @PostMapping("/saveAccount")
    @ApiIgnore
    fun saveAccount(@RequestBody account: Account): Account? {
        return accountService.saveAccount(account)
    }

    @GetMapping("/retrieveToken")
    @ApiIgnore
    fun retrieveToken(): String {
        return tokenRetrieverService.retrieveToken()
    }

    @GetMapping("/accessTokenByAccountNumber/{accountNumber}")
    @ApiIgnore
    fun accessTokenByAccountNumber(@PathVariable accountNumber: String): AccessToken? {
        return accessTokenService.findByAccountNumber(accountNumber)
    }

    @GetMapping("/isAccessTokenExpired/{accountNumber}")
    @ApiIgnore
    fun isAccessTokenExpired(@PathVariable accountNumber: String): Boolean? {
        return accessTokenService.isAccessTokenExpired(accountNumber)
    }

    @GetMapping("/saveAccessToken/{token}/{accountNumber}")
    @ApiIgnore
    fun saveAccessToken(@PathVariable token: String, @PathVariable accountNumber: String) {
        return accessTokenService.saveAccessToken(token,accountNumber)
    }
}


