package dev.aventix.station.authserver.user.session

import dev.aventix.station.authserver.user.session.SessionRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SessionService(
    private val sessionRepository: SessionRepository,
) {
//    @Scheduled(initialDelay = 2000, fixedDelay = 2000)
//    fun printSessions() {
//        val result = sessionRepository.findByPrincipalName("one")
//        if (result == null) {
//            println("User is not online")
//        } else {
//            println("The user is online!")
//        }
//    }
}