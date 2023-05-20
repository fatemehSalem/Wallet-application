package com.micro.otp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class OtpApplication

fun main(args: Array<String>) {
    runApplication<OtpApplication>(*args)
}
