package dev.aventix.station.authserver.user.session

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "spring_session")
class SpringSession {

    @Id
    @Column(name = "primary_id", nullable = false, unique = true)
    lateinit var id: String

    @Column(name = "session_id", nullable = false, unique = true)
    lateinit var sessionId: String

    @Column(name = "creation_time", nullable = false)
    var creationTime: Long = 0

    @Column(name = "last_access_time", nullable = false)
    var lastAccessTime: Long = 0

    @Column(name = "max_inactive_interval", nullable = false)
    var maxInactiveInterval: Long = 0

    @Column(name = "expiry_time", nullable = false)
    var expiryTime: Long = 0

    @Column(name = "principal_name")
    var principalName: String? = null

}