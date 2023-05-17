package com.micro.account.config

/*import com.micro.account.service.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class AuthFilter : WebFilter, Ordered {
    @Autowired
    private lateinit var tokenService: TokenService
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val token = "" *//*tokenService.fndByUserEmail()*//*
            exchange.request.mutate()
                .headers { headers ->
                    headers.setBearerAuth(token)
                }
                .build()

        return chain.filter(exchange)
    }

    override fun getOrder(): Int {
        return Ordered.HIGHEST_PRECEDENCE
    }
}*/

