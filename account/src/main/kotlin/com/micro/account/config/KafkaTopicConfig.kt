package com.micro.account.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaTopicConfig {
    @Bean
    fun newTopic(): NewTopic = TopicBuilder.name("account_to_transaction").build()
}