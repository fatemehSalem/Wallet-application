package com.micro.auth.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "jwtToken")

data class JwtToken(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @Column(name = "jwt_token", nullable = false)
    var jwtToken: String,

    @Column(name = "jwt_refresh_token", nullable = true )
    var jwtRefreshToken: String,

    @Column(name = "user_phone_number", nullable = false , unique = true)
    var userPhoneNumber: String
){
    constructor() : this(null, "" , "", "") {
    }
}
