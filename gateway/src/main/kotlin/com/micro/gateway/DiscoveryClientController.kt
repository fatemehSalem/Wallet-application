package com.micro.gateway

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.net.URI


@RestController
class DiscoveryClientController (
    private val clientBuilder: WebClient.Builder
){

    @GetMapping("/order")
    fun placeOrder() {
        val client = clientBuilder.baseUrl("http://order/place").build()
        client.get().retrieve().bodyToMono(String::class.java).subscribe { println(it) }
    }


}
