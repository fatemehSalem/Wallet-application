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

    @Column(name = "user_id", unique = true)
    var userId: String,

    @Column(name = "user_phone_number",  unique = true)
    var userPhoneNumber: String,

    @Column(name = "from_Login_Or_SignUp",  unique = true)
    //from signUp = 1
    //from login = 0
    var fromLoginOrSignUp: String,



/*    @Column(name = "user_email", unique = true)
    var userEmail: String,*/

    ) {
    constructor() : this(null , "" , "","" , "") {
    }
}
