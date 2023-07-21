package com.micro.auth.service

import com.micro.auth.util.JwtUtil
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*


@Component
class JwtTokenService(
    @Value("\${jwt.secret}")
    private val secretKey: String)
{
    @Autowired
    private val authenticationManager: AuthenticationManager? = null

    @Autowired
    private val jwtTokenUtil: JwtUtil? = null

    @Autowired
    private val userDetailsService: UserDetailsService? = null

    private val jwtParser = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secretKey.toByteArray())).build()

    fun generateTokens(username: String): Map<String, String> {
        val accessTokenExpiration = Instant.now().plus(1, ChronoUnit.WEEKS)
        val refreshTokenExpiration = Instant.now().plus(1, ChronoUnit.MONTHS)

        val accessToken = Jwts.builder()
            .setSubject(username)
            .setExpiration(Date.from(accessTokenExpiration))
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()), SignatureAlgorithm.HS256)
            .compact()

        val refreshToken = Jwts.builder()
            .setSubject(username)
            .setExpiration(Date.from(refreshTokenExpiration))
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()), SignatureAlgorithm.HS256)
            .compact()

        return mapOf("accessToken" to accessToken, "refreshToken" to refreshToken)
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims: Jws<Claims> = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.toByteArray()))
                .build()
                .parseClaimsJws(token)

            true
        } catch (ex: Exception) {
            // Token verification failed
            false
        }
    }

    //create auth token
    @Throws(java.lang.Exception::class)
    fun createAuthenticationToken(phoneNumber: String): String? {
        try {
            authenticationManager?.authenticate(
                UsernamePasswordAuthenticationToken(phoneNumber, "")
            )
        } catch (e: BadCredentialsException) {
            throw java.lang.Exception("Incorrect username or password", e)
        }
        var userDetails: UserDetails = userDetailsService!!.loadUserByUsername(phoneNumber)
        return jwtTokenUtil?.generateToken(userDetails)

    }
}
