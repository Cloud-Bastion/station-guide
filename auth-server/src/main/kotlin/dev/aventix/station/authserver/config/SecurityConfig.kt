package dev.aventix.station.authserver.config

import com.nimbusds.jose.jwk.JWKSelector
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class SecurityConfig(
    private val applicationConfig: ApplicationConfigProperties
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .cors {
                it.configurationSource(UrlBasedCorsConfigurationSource().apply {
                    registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues().apply {
                        addAllowedMethod(HttpMethod.OPTIONS)
                        addAllowedMethod(HttpMethod.POST)
                        addAllowedMethod(HttpMethod.PATCH)
                        addAllowedMethod(HttpMethod.DELETE)
                        addAllowedMethod(HttpMethod.GET)
                        addAllowedOrigin("http://localhost:5173")
                    })
                })
            }
            .authorizeHttpRequests {
                auth -> auth
                .anyRequest().permitAll()
                /*.requestMatchers("/api/v1/auth/login").permitAll()
                .anyRequest().authenticated()*/
            }
            .csrf { csrf -> csrf.disable() }
            .sessionManagement { session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .build()
    }

//    @Bean
//    fun passwordEncoder(): PasswordEncoder = Argon2PasswordEncoder(
//        16, // salt length in bytes
//        32, // hash length in bytes
//        1, // parallelism
//        65536, // 64MB memory cost
//        10 // work-factor, adjust to hardware
//    )
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(userDetailsService: UserDetailsService): AuthenticationManager {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(userDetailsService)
        return ProviderManager(provider)
    }

    @Bean
    fun jwtDecoder(): JwtDecoder = NimbusJwtDecoder.withPublicKey(applicationConfig.rsaPublicKey).build()

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val rsaKey = JwtKeys.generateRsa()
        val jwkSet = JWKSet(rsaKey)
        return JWKSource { jwkSelector: JWKSelector, _: SecurityContext? -> jwkSelector.select(jwkSet) }
    }

    @Bean
    fun jwkEncoder(source: JWKSource<SecurityContext>) = NimbusJwtEncoder(source)

}