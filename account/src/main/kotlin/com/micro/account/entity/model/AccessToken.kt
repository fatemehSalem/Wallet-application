package com.micro.account.entity.model

import javax.persistence.*
import java.time.Instant

@Entity
@Table(name = "accessToken")

data class AccessToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(name = "access_token", nullable = false, length = 1000)
    var accessToken: String,

    @Column(name = "account_number", nullable = false )
    var accountNumber: String,

    @Column(name = "creation_Time", nullable = false)
    var creationTime: Instant = Instant.now(),

    @Column(name = "expiration_Time",nullable = false)
    var expirationTime: Instant = Instant.now()
) {
    //to fix the error: Class 'Token' should have [public, protected] no-arg constructor error
    constructor() : this(0, "", "" )
}
