package com.micro.account.entity

import lombok.*
import javax.persistence.*

@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table( name = "account")
class Account {
  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    /* @Column(nullable = true)
   var account_number: String,

   @Column(nullable = false)
   var currency_code: String,

   var alias: String,
   var user_number: String,
   var user_first_name: String,
   var user_last_name: String,
   var user_phone_country_code: String,
   var user_phone_number: String,
   var user_email: String,
   var address_line1: String,
   var address_line2: String,
   var zip_postal_code: String,
   var state_province_code: String,*/

}