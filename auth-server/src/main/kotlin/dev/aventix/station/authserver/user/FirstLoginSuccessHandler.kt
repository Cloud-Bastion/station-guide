package dev.aventix.station.authserver.user

import dev.aventix.station.authserver.user.spring.StationUserDetails
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import java.io.IOException


class FirstLoginSuccessHandler: SavedRequestAwareAuthenticationSuccessHandler() {

    @Throws(ServletException::class, IOException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest, response: HttpServletResponse?,
        authentication: Authentication
    ) {
        // 1. Get the authenticated principal (your user object)

        val principal: Any? = authentication.principal
        var passwordChangeRequired = false

        // 2. Check your custom attribute
        // Replace 'LocalAppUser' with the actual class of your principal
        if (principal is StationUserDetails) { // Use your custom user details class
            val user: StationUserDetails = principal
            if (!user.isPasswordChanged()) { // Check the flag
                passwordChangeRequired = true
            }
        }

        // Add checks for other principal types if necessary (e.g., OidcUser for social logins)
        // else if (principal instanceof OidcUser) { ... }

        // 3. Perform the redirect if needed
        if (passwordChangeRequired) {
            // Clear any previously saved request to avoid redirecting back to /oauth2/authorize
            clearAuthenticationAttributes(request)
            // Redirect to your password change page
            redirectStrategy.sendRedirect(request, response, "/change-password")
        } else {
            // If no password change is needed, proceed with the default behavior
            // (e.g., redirect to the original page that triggered the login)
            super.onAuthenticationSuccess(request, response, authentication)
        }
    }

}