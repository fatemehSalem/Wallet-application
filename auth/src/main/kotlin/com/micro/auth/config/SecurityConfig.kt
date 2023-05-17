/*
package com.micro.auth.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@Configuration
@EnableWebSecurity
class SecurityConfig(private val customAuthProvider: CustomAuthProvider) : WebSecurityConfigurer() {

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(customAuthProvider)
    }

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .httpBasic()
    }
}
*/
