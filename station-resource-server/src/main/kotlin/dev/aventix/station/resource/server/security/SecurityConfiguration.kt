package dev.aventix.station.resource.server.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
class SecurityConfiguration {

    @Bean
    @Throws(Exception::class)
    fun resourceServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http // Enable and configure CORS
            .cors {
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
            } // Uses corsFilter bean below
            // Disable CSRF for stateless REST APIs

            .csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() } // Configure session management to be stateless

            .sessionManagement { session: SessionManagementConfigurer<HttpSecurity?> ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            } // Configure authorization rules

            .authorizeHttpRequests { authorize ->
                authorize // Example: Allow public access to actuator health endpoint
                    .requestMatchers("/actuator/health").permitAll() // Secure the /api/messages endpoint
                    //.requestMatchers("/api/greet")
                    //.hasAuthority("SCOPE_message.read") // Requires 'message.read' scope
                    // Secure any other /api/** endpoints - require at least authentication
                    .requestMatchers("/api/**").authenticated() // Deny any other requests by default
                    .anyRequest().authenticated()
            } // Or .authenticated() if applicable
            // Configure OAuth2 Resource Server with JWT validation

            .oauth2ResourceServer { oauth2: OAuth2ResourceServerConfigurer<HttpSecurity?> ->
                oauth2
                    .jwt(Customizer.withDefaults())
            } // Enables JWT validation based on application.yml properties


        return http.build()
    }

//    @Bean
//    @Throws(Exception::class)
//    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
//        return http.authorizeHttpRequests { auth ->
//            auth.anyRequest().permitAll()
//        }.oauth2ResourceServer { resourceServer ->
//            resourceServer.jwt(Customizer.withDefaults())
//        }.cors {
//            it.configurationSource(UrlBasedCorsConfigurationSource().apply {
//                registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues().apply {
//                    addAllowedMethod(HttpMethod.OPTIONS)
//                    addAllowedMethod(HttpMethod.POST)
//                    addAllowedMethod(HttpMethod.PATCH)
//                    addAllowedMethod(HttpMethod.DELETE)
//                    addAllowedMethod(HttpMethod.GET) // Added GET for completeness
//                    addAllowedOrigin("https://dashboard.cakmak-station.de")
//                    addAllowedOrigin("http://localhost:5173")
//                    //allowCredentials = true
//                    // addAllowedHeader("*") // Example: Allow all headers
//                })
//            })
//        }.csrf {
//            // Disable CSRF, common for stateless APIs using tokens
//            it.disable()
//        }.build()
//    }

}
