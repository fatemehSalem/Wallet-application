package com.micro.account.controller

import com.micro.account.entity.ErrorCode
import com.micro.account.entity.model.OTPCheck
import com.micro.account.entity.request.AccountRequest
import com.micro.account.entity.request.ChangeAccountPasswordRequest
import com.micro.account.entity.request.LoginRequest
import com.micro.account.entity.response.CustomResponse
import com.micro.account.entity.request.GenerateOtpCodeRequest
import com.micro.account.entity.response.LoginResponse
import com.micro.account.service.AccountService
import com.micro.account.service.OtpService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/account")
@Api(value = "Account API", description = "Account Service APIs")
class AccountController {

    @Autowired
    private lateinit var accountService: AccountService

    @Autowired
    private lateinit var otpService: OtpService


    @PostMapping("/login")
    @ApiOperation("Account Login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Any> {
        if (accountService.authenticate(loginRequest.user_phone_number, loginRequest.user_password)) {
            val response = accountService.findByUserPhoneNumber(loginRequest.user_phone_number)
                ?.let {
                    CustomResponse(
                        LoginResponse(
                            it.accountNumber,
                            accountService.generateOtpForLogin(loginRequest.user_phone_number)
                        ), "Account Authentication was Successful"
                    )
                }
            return ResponseEntity.ok(response)
        }
        val errorCode = ErrorCode.ACCOUNT_AUTHENTICATION_WAS_UNSUCCESSFUL
        val response = CustomResponse(null, "Account Authentication for login was unsuccessful", errorCode.code)
        return ResponseEntity.ok(response)

    }

    //****Should implement in OTP service****
    @PostMapping("/generateNewOtpCode")
    @ApiOperation("Account authenticate")
    fun generateNewOtpCodeAfterExpiration(@RequestBody request: GenerateOtpCodeRequest): ResponseEntity<Any> {
        return otpService.generateNewOtpCodeAfterExpiration(request)
    }


    @PostMapping("/authenticate")
    @ApiOperation("Account authenticate")
    fun login(@RequestBody otpCheck: OTPCheck): ResponseEntity<Any> {
        return accountService.verifyOTP(otpCheck)
    }

    @PostMapping("/register")
    @ApiOperation("Register New Account")
    fun createAccountBeforeOTP(@RequestBody accountRequest: AccountRequest): ResponseEntity<Any> {
        //1_Account info stored in Memory(Redis) and OTP code generated
        return accountService.createNewAccountInMemory(accountRequest)
        // }
    }

    @PostMapping("/signup")
    @ApiOperation("Account SignUp")
    fun createAccountBeforeOTP(@RequestBody otpCheck: OTPCheck): ResponseEntity<Any> {
        //2_Account info stored in db
        return accountService.createNewAccount(otpCheck)
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


}


