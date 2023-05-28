package com.micro.account.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "accessToken")

data class Token(
    @Id
    @SequenceGenerator(
        name = "token_id_sequence",
        sequenceName = "token_id_sequence"
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "token_id_sequence"
    )
    var id: UUID? = null,

    @Column(name = "access_token", nullable = false,  unique = true)
    var accessToken: String,

    @Column(name = "user_phone_number", nullable = false , unique = true)
    var userPhoneNumber: String,
) {
    //to fix the error: Class 'Token' should have [public, protected] no-arg constructor error
    constructor() : this(null, "", "")
}
