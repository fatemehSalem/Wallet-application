package com.micro.auth.config

import com.micro.auth.util.JwtRequestFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
internal class WebSecurityConfig{


    @Autowired
    private val jwtRequestFilter: JwtRequestFilter? = null

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

/*    private val unauthorizedHandler: AuthEntryPointJwt? = null

    private val authenticationJwtTokenFilter: AuthTokenFilter? = null
*/
    @Autowired
    @Throws(java.lang.Exception::class)
    open fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService<UserDetailsService>(userDetailsService)
    }


    @Bean
    @Throws(java.lang.Exception::class)
    fun authenticationManager(builder: AuthenticationManagerBuilder): AuthenticationManager? {
        return builder.userDetailsService<UserDetailsService?>(userDetailsService).passwordEncoder(encoder()).and()
            .build()
    }

    @Bean
    fun encoder(): PasswordEncoder? {
        return BCryptPasswordEncoder()
    }

/*    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }*/
/*    @Bean
    @Throws(Exception::class)
     fun  configure(httpSecurity: HttpSecurity) {
        httpSecurity.csrf().disable()
            .authorizeRequests().antMatchers(*WHITELIST).permitAll().anyRequest().authenticated().and()
            .exceptionHandling().and().sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        httpSecurity.headers().frameOptions().disable()
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
    }*/

    @Bean
    @Throws(Exception::class)
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain? {
        /*http.cors().and().csrf().disable()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
            .antMatchers(UrlMapping.AUTH + UrlMapping.SIGN_UP).permitAll()
            .antMatchers(UrlMapping.AUTH + UrlMapping.LOGIN).permitAll()
            .antMatchers(UrlMapping.VALIDATE_JWT).permitAll()
            .antMatchers(*WHITELIST).permitAll()
            .anyRequest().authenticated()
       *//* http.addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)*//*
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)*/
        httpSecurity.csrf().disable()
            .authorizeRequests().antMatchers(*WHITELIST).permitAll().anyRequest().authenticated().and()
            .exceptionHandling().and().sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        httpSecurity.headers().frameOptions().disable()
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
        return httpSecurity.build()
    }

    @Bean
    fun corsConfigurer(): WebMvcConfigurer? {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowedMethods("*")
            }
        }
    }

    companion object {
        private val WHITELIST = arrayOf(
            "/api/account/login",
            "/api/otp/**",
            "/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**"
        )
    }
}