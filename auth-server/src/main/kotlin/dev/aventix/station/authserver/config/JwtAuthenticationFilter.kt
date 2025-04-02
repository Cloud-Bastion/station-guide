package dev.aventix.station.authserver.config

import dev.aventix.station.authserver.authenticate.TokenService
import dev.aventix.station.authserver.user.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.NegatedRequestMatcher
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val tokenService: TokenService,
    private val userService: UserService,
): OncePerRequestFilter() {

    private val jwtLogger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return request.requestURI.endsWith("/auth/login")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        jwtLogger.debug("Running JWT authentication filter")


        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        jwtLogger.debug("Request header contains authorization bearer token.")

        val token = authHeader.substring(7)
        val username = tokenService.extractUsername(token)

        jwtLogger.debug("Fetched username from JWT token.")

        if (SecurityContextHolder.getContext().authentication == null) {
            jwtLogger.debug("The user has not been authenticated before, setting authentication for the current session.")

            val userDetails = userService.loadUserByUsername(username)
            if (tokenService.validateToken(token, userDetails)) {
                val authToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                SecurityContextHolder.getContext().authentication = authToken
                jwtLogger.debug("Authentication set in security context.")
            }
        }

        filterChain.doFilter(request, response)
    }

}