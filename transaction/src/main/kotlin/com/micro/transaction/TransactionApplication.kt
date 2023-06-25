package com.micro.transaction

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class TransactionApplication

fun main(args: Array<String>) {
    runApplication<TransactionApplication>(*args)
}
