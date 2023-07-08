/*
package com.micro.transaction.entity.model
import org.hibernate.Hibernate
import javax.persistence.*
import java.time.LocalDateTime
@Entity
@Table(name = "account")
data class KafkaReceivedAccount(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column
    var accountNumber: String,

    @Column
    var walletNumber: String,

    @Column
    var walletId: String,

    @Column
    var userFirstName: String,

    @Column
    var userLastName: String,

    @Column
    var userPhoneNumber: String,

    @Column
    var userEmail: String,

    @Column
    var createdAt: LocalDateTime = LocalDateTime.now(),

) {
    constructor() : this(0, "" , "" , "","","","","") {
    }
}
*/
