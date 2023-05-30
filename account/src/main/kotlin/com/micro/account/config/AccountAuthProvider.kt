package com.micro.account.config
/*
import com.micro.account.entity.LoginResponse
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class AccountAuthProvider : AuthenticationProvider {
    private val restTemplate = RestTemplate()

    override fun authenticate(authentication: Authentication): Authentication? {
        val username = authentication.name
        val password = authentication.credentials.toString()

        val url = "https://idsuat.walletgate.io/connect/token"
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.setBasicAuth("client_id", "client_secret")
        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("grant_type", "password")
        params.add("username", username)
        params.add("password", password)
        val request = HttpEntity(params, headers)
        val response = restTemplate.postForEntity(url, request, LoginResponse::class.java)

        // Extract the access token from the response and create a new token object
        val accessToken = response.body?.access_token
        //save accesstoken
        val authorities = mutableListOf<SimpleGrantedAuthority>()
        authorities.add(SimpleGrantedAuthority("ROLE_USER"))
        val token = UsernamePasswordAuthenticationToken(username, password, authorities)

        // Set the access token as the details of the token object
        token.details = accessToken

        return token
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}*/

/*
@Component
class AccountAuthProvider(private val accountRepository: AccountRepository) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.name
        val password = authentication.credentials.toString()

        val user = accountRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("Invalid username or password")

        if (!BCrypt.checkpw(password, account.password)) {
            throw BadCredentialsException("Invalid username or password")
        }

        return UsernamePasswordAuthenticationToken(user, null, user.authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}*/