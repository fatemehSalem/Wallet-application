package com.micro.account.controller

import com.micro.account.entity.dto.P2PTransferRequestDto
import com.micro.account.entity.dto.TopUpCreditCardDto
import com.micro.account.service.TransactionService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/transaction")
@Api(value = "Transaction API", description = "Transaction Service APIs")
class TransactionController {
    @Autowired
    private lateinit var transactionService: TransactionService

    @PostMapping("/personalToPersonalTransfer")
    @ApiOperation("Personal To Personal Transfer")
    fun personalToPersonalTransfer(@RequestBody request: P2PTransferRequestDto): ResponseEntity<Any> {
        return transactionService.personalToPersonalTransfer(request)
    }

    @PostMapping("/topUpCreditCard")
    @ApiOperation("Top Up Credit Card")
    fun topUpCreditCard(@RequestBody request: TopUpCreditCardDto): ResponseEntity<Any> {
        return transactionService.topUpCreditCard(request)
    }

    @GetMapping("/transactionHistory/{accountNumber}")
    @ApiOperation("Get Transaction History By Account_Number")
    fun transactionHistory(@PathVariable accountNumber: String): ResponseEntity<Any> {
        return transactionService.transactionHistory(accountNumber)
    }

    @GetMapping("/transactionDetail/{transactionId}/{accountNumber}")
    @ApiOperation("Get Transaction Detail By Transaction_Id and Account_Number")
    fun transactionDetail(@PathVariable transactionId: String,@PathVariable accountNumber: String ): ResponseEntity<Any> {
        return transactionService.transactionDetail(transactionId, accountNumber)
    }
}