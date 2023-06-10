package com.micro.account.entity.model

import javax.persistence.*

@Entity
@Table(name = "otp")
data class OTP (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(name = "user_phone_number")
    var userPhoneNumber: String,

    @Column(name = "user_OTP_code")
    var userOtpCode: String

){
    constructor() : this(0, "", "")
}