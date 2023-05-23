package com.micro.account.entity

import java.util.*
import javax.persistence.*


@Entity
@Table(name = "account")

data class Account (
/* By default, properties of a data class are declared as val (read-only)
which means that they are immutable. However, you can still declare a var property in a data class if you need to.*/
  @Id
  @GeneratedValue
  var id: UUID? = null,
 /* @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long? = 0,*/

  @Column(name = "account_number", nullable = true)
  var accountNumber: String,

  @Column(name = "password", nullable = false)
  var password: String,

  @Column(name = "currency_code", nullable = false)
  var currencyCode: String,

  @Column(name = "alias", nullable = true)
  var alias: String,

  @Column(name = "user_number", nullable = true ,  unique = true)
  var userNumber: String,

  @Column(name = "user_first_name", nullable = false)
  var userFirstName: String,

  @Column(name = "user_last_name", nullable = false)
  var userLastName: String,

  @Column(name = "user_phone_country_code", nullable = false)
  var userPhoneCountryCode: String,

  @Column(name = "user_phone_number", nullable = false , unique = true)
  var userPhoneNumber: String,

  @Column(name = "user_email", nullable = true,  unique = true)
  var userEmail: String,

  @Column(name = "address_line1", nullable = true)
  var addressLine1: String,

  @Column(name = "address_line2", nullable = true)
  var addressLine2: String,

  @Column(name = "zip_postal_code", nullable = true)
  var zipPostalCode: String,

  @Column(name = "state_province_code", nullable = true)
  var stateProvinceCode: String
){
  constructor() : this(null, "", "", "",
  "","","","","","","",
            "","" ,"","")
}