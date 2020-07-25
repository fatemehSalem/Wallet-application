package com.micro.gateway

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.cloud.gateway.route.builder.PredicateSpec
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate


@Configuration
class GatewayConfiguration {

    @Bean
    fun routeConfiguration(@Autowired restTemplate: RestTemplate, builder: RouteLocatorBuilder): RouteLocator? {
        return builder.routes()
                .route { p: PredicateSpec -> p
                    .path("/")
                    .filters { f: GatewayFilterSpec -> f.addRequestHeader("Hello", "World") }
                    .uri("http://order")
                }.build()
    }

    @LoadBalanced
    @Bean
    fun loadbalancedRestTemplate(): RestTemplate {
        return RestTemplate();
    }
}