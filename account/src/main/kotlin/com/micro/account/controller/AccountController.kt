package com.micro.account.controller

import com.micro.account.entity.*
import com.micro.account.service.AccountService
import com.micro.account.utils.GeneralUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/account")
//@Api(tags = ["Account Controller"])
class AccountController {

    @Autowired
    private lateinit var accountService: AccountService

    @GetMapping("/testAccountService/{num}")
    fun test(@PathVariable num: String):String{
        return "testAccountService is ran!!"
    }
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest):ResponseEntity<CustomResponse<Any>>{
        if (accountService.authenticate(loginRequest.phoneNumber ,loginRequest.password )) {
            accountService.generateOtp(loginRequest.phoneNumber)
            return ResponseEntity.status(HttpStatus.OK).
            body(
                GeneralUtils.createCustomResponse(
                    "",
                    "Account Authentication was Successful", "200"
                )
            )
        }
        val errorCode = ErrorCode.ACCOUNT_AUTHENTICATION_WAS_UNSUCCESSFUL
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            GeneralUtils.createCustomResponse(
                "",
                "Account Authentication was unsuccessful", errorCode.code
            )
        )
      //  return ResponseEntity("Authentication failed", HttpStatus.UNAUTHORIZED)
    }

/*    @PostMapping("/login")
    @ApiOperation("Get Login Request")
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
    }*/

    //After successful Authentication
//    @PostMapping("/generateOtp")
//    @ApiOperation("Generate OTP")
//    fun generateOtp(@RequestBody request: GenerateOtpRequest) {
//        return accountService.generateOtp(request)
//    }

    @PostMapping("/createNewAccountBeforeOTP")
//    @ApiOperation("Create New Account")
  fun createAccountBeforeOTP(@RequestBody accountRequest: AccountRequest): ResponseEntity<CustomResponse<Any>> {
            //1_Account info stored in Memory and OTP code generated
           return accountService.createNewAccountInMemory(accountRequest)
            // }
    }

    @PostMapping("/createNewAccountAfterOTP")
    fun createAccountBeforeOTP(@RequestBody otpCheck: OTPCheck): ResponseEntity<CustomResponse<Any>>{
       return  accountService.createNewAccount(otpCheck)
        // return accountResponseMono.map { accountResponse ->
    }

    @PostMapping("/verifyOTP")
    fun login(@RequestBody otpCheck: OTPCheck):ResponseEntity<CustomResponse<Any>>{
        return accountService.verifyOTP(otpCheck)
    }

    @GetMapping("/findByUserPhoneNumber/{phoneNumber}")
        fun findByUserPhoneNumber(@PathVariable phoneNumber: String): LoginRequest? {
        val account = accountService.findByUserPhoneNumber(phoneNumber)
        var loginRequest = LoginRequest("" , "")
        if (account != null) {
             loginRequest = LoginRequest(account.password, account.userPhoneNumber)
        }
        return loginRequest
    }

    @GetMapping("/getAccountInfo/{phoneNumber}")
    fun getAccountInfo(@PathVariable phoneNumber: String): ResponseEntity<CustomResponse<Any>>{
        return  accountService.getAccountInfo(phoneNumber)
    }

}


