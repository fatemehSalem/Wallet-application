package com.micro.transaction.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate


@Configuration
class TransactionModuleConfig {
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

}