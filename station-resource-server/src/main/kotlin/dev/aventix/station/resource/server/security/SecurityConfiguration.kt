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
            auth.anyRequest().permitAll()
        }.oauth2ResourceServer { resourceServer ->
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
                    //allowCredentials = true
                    // addAllowedHeader("*") // Example: Allow all headers
                })
            })
        }.csrf {
            // Disable CSRF, common for stateless APIs using tokens
            it.disable()
        }.build()
    }

}
