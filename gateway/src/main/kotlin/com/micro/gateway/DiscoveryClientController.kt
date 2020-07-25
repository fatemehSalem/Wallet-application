package com.micro.gateway

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI


@RestController
class DiscoveryClientController {
    @Autowired
    private val discoveryClient: DiscoveryClient? = null

    fun serviceUrl(): URI? {
        val list = discoveryClient!!.getInstances("order")
        return if (list != null && list.size > 0) {
            list[0].uri
        } else null
    }

    @GetMapping("services")
    fun displaySerices() {
        println(serviceUrl().toString())
    }
}
