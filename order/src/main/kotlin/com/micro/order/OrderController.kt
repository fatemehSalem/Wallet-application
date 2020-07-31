package com.micro.order

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderController {

    @GetMapping("/place")
    fun order(): String {
        println("ORDER ORDER ORDER")
        return "ORDER PLACED"
    }
}