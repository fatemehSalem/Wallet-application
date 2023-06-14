package com.micro.account.entity.model

import java.time.LocalDateTime
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
    var userOtpCode: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    ){
    constructor() : this(0, "", "")
}