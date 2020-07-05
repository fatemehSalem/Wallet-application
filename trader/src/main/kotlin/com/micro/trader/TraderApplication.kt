package com.micro.trader

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TraderApplication

fun main(args: Array<String>) {
	runApplication<TraderApplication>(*args)
}
