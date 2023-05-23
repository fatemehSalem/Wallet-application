package com.micro.otp.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "otpUserCode")
data class OtpUserCode(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @Column(name = "user_code", unique = true)
    var otpCode: String,

    @Column(name = "user_phone_number",  unique = true)
    var userPhoneNumber: String,

/*    @Column(name = "user_email", unique = true)
    var userEmail: String,*/

    ) {
    constructor() : this(null , "" , "") {
    }
}
