package dev.aventix.station.resource.server.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.security.config.Customizer

@Configuration
class SecurityConfiguration {
    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http.authorizeHttpRequests { auth ->
            // Require authentication for all API endpoints under /api/v1/
            auth.requestMatchers("/api/v1/**").authenticated()
            // Allow all other requests (e.g., actuator, swagger-ui if added later)
            // Consider securing these endpoints as needed in the future.
            auth.anyRequest().permitAll()
        }.oauth2ResourceServer { resourceServer ->
            // Configure JWT validation using the jwk-set-uri from application.yml
            resourceServer.jwt(Customizer.withDefaults())
        }.cors {
            it.configurationSource(UrlBasedCorsConfigurationSource().apply {
                registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues().apply {
                    addAllowedMethod(HttpMethod.OPTIONS)
                    addAllowedMethod(HttpMethod.POST)
                    addAllowedMethod(HttpMethod.PATCH)
                    addAllowedMethod(HttpMethod.DELETE)
                    addAllowedMethod(HttpMethod.GET) // Added GET for completeness
                    addAllowedOrigin("https://dashboard.cakmak-station.de")
                    addAllowedOrigin("http://localhost:5173")
                    // Allow credentials (cookies, authorization headers) if needed
                    // allowCredentials = true
                    // Allow specific headers if needed
                    // addAllowedHeader("*") // Example: Allow all headers
                })
            })
        }.csrf {
            // Disable CSRF, common for stateless APIs using tokens
            it.disable()
        }.build()
    }

}
