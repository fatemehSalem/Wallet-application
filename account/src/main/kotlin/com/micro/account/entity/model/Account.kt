package com.micro.account.entity.model

import javax.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "account")

data class Account (
/* By default, properties of a data class are declared as val (read-only)
which means that they are immutable. However, you can still declare a var property in a data class if you need to.*/
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Int,

  @Column(name = "account_number")
  var accountNumber: String,

  @Column(name = "wallet_number")
  var walletNumber: String,

  @Column(name = "wallet_id")
  var walletId: String,

  @Column(name = "password")
  var password: String,

  @Column(name = "currency_code")
  var currencyCode: String,

  @Column(name = "alias")
  var alias: String,

  @Column(name = "user_number")
  var userNumber: String,

  @Column(name = "user_first_name")
  var userFirstName: String,

  @Column(name = "user_last_name")
  var userLastName: String,

  @Column(name = "user_phone_country_code")
  var userPhoneCountryCode: String,

  @Column(name = "user_phone_number")
  var userPhoneNumber: String,

  @Column(name = "user_email")
  var userEmail: String,

  @Column(name = "address_line1")
  var addressLine1: String,

  @Column(name = "address_line2")
  var addressLine2: String,

  @Column(name = "zip_postal_code")
  var zipPostalCode: String,

  @Column(name = "state_province_code")
  var stateProvinceCode: String,

  @Column(name = "created_at", nullable = false, updatable = false)
  var createdAt: LocalDateTime = LocalDateTime.now(),

  @Column(name = "updated_at")
  var updatedAt: LocalDateTime = LocalDateTime.now()
){
  constructor() : this(0, "", "", "", "",
            "","","","","",
     "","",
              "","" ,"","","")
}