package com.micro.transaction.controller

import com.micro.transaction.entity.dto.P2PTransferRequestDto
import com.micro.transaction.entity.dto.TopUpCreditCardDto
import com.micro.transaction.service.P2PTransferService
import com.micro.transaction.service.TopUpCreditCardService
import com.micro.transaction.service.TransactionDetailService
import com.micro.transaction.service.TransactionHistoryService
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
    private lateinit var topUpCreditCardService: TopUpCreditCardService

    @Autowired
    private lateinit var p2PTransferService: P2PTransferService

    @Autowired
    private lateinit var transactionHistoryService: TransactionHistoryService

    @Autowired
    private lateinit var transactionDetailService: TransactionDetailService

    @PostMapping("/personalToPersonalTransfer")
    @ApiOperation("Personal To Personal Transfer")
    fun personalToPersonalTransfer(@RequestBody request: P2PTransferRequestDto): ResponseEntity<*> {
        p2PTransferService.sendFindAccountsByAccountNumbers(request)
        val listenerResponse = p2PTransferService.getResponseFuture()
        return listenerResponse.get()
    }

    @PostMapping("/topUpCreditCard")
    @ApiOperation("Top Up Credit Card")
    fun topUpCreditCard(@RequestBody request: TopUpCreditCardDto): ResponseEntity<*> {
        topUpCreditCardService.sendFindAccountByAccountNumber(request)
        val listenerResponse = topUpCreditCardService.getResponseFuture()
        return listenerResponse.get()

    }

    @GetMapping("/transactionHistory/{accountNumber}")
    @ApiOperation("Get Transaction History By account_number")
    fun transactionHistory(@PathVariable accountNumber: String) : ResponseEntity<*>{
        transactionHistoryService.sendFindAccountByAccountNumber(accountNumber)
        val listenerResponse = transactionHistoryService.getResponseFuture()
        return listenerResponse.get()
    }


    @GetMapping("/transactionDetail/{transactionId}/{accountNumber}")
    @ApiOperation("Get Transaction Detail By transaction_id and account_number")
    fun transactionDetail(@PathVariable transactionId: String, @PathVariable accountNumber: String ) : ResponseEntity<*>{
         transactionDetailService.sendFindAccountByAccountNumber(transactionId,accountNumber)
        val listenerResponse = transactionDetailService.getResponseFuture()
        return listenerResponse.get()
    }
}