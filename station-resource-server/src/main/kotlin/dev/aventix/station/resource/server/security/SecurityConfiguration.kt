package dev.aventix.station.resource.server.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.config.http.SessionCreationPolicy

@EnableMethodSecurity
@Configuration
class SecurityConfiguration {

    @Bean
    @Throws(Exception::class)
    fun resourceServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors {
                it.configurationSource(UrlBasedCorsConfigurationSource().apply {
                    registerCorsConfiguration("/**", CorsConfiguration().apply {
                        allowedOriginPatterns = listOf("https://*.cakmak-station.de", "http://localhost:*")
                        allowedHeaders = listOf("*")
                        allowedMethods = listOf("*")
                        allowCredentials = true
                    })
                })
            }
            .csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/actuator/health").permitAll()
                    .requestMatchers("/api/**").authenticated()
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2: OAuth2ResourceServerConfigurer<HttpSecurity?> ->
                oauth2
                    .jwt(Customizer.withDefaults())
            }
        return http.build()
    }
}
