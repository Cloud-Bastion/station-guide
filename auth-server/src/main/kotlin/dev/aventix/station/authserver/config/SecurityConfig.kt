package dev.aventix.station.authserver.config

import com.nimbusds.jose.jwk.JWKSelector
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey // Import RSAKey
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory.disable
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder // Correct import for NimbusJwtEncoder
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings // Import AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint // Import for login redirect
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.*

@Configuration
class SecurityConfig(
    private val applicationConfig: ApplicationConfigProperties,
) {
    @Bean
    @Order(1) // Authorization server filter chain needs higher precedence
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.securityMatcher("/oauth2/**", "/api/v1/auth/**") // ✅ Apply filter chain ONLY to OAuth2 endpoints
        http.formLogin { disable() }
        http.httpBasic { disable() }

        http.with(OAuth2AuthorizationServerConfigurer.authorizationServer(), Customizer.withDefaults())
            .getConfigurer(OAuth2AuthorizationServerConfigurer::class.java)
            .oidc(Customizer.withDefaults()) // Enable OpenID Connect if needed

        http.cors(Customizer.withDefaults())
            .exceptionHandling { exceptions ->
                exceptions.authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login"))
            }

        return http.build()
    }


    @Bean
    @Order(2) // Default security filter chain
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.cors(Customizer.withDefaults()) // Apply CORS globally
            .authorizeHttpRequests { auth ->
                // Allow access to the login page and error pages (used by Spring Security form login)
                auth
                    .requestMatchers("/api/v1/auth/**", "/error").permitAll()
                    // Secure all other endpoints handled by this chain (e.g., custom API endpoints if any)
                    .anyRequest().authenticated()
            }
            .csrf { csrf -> csrf.disable() } // Disable CSRF for stateless API
            .sessionManagement { session ->
                // ROPC is stateless, but form login (if used as fallback or for other flows) might need session.
                 session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            }

            // NO DISABLE FORM LOGIN!!
            // We can then send a request to /login endpoint with username/pwd set as header
            // Configure form login (used if user hits a protected resource without auth, or for /oauth2/authorize)
            // .formLogin(Customizer.withDefaults())

        return http.build()
    }


    @Bean
    fun registeredClientRepository(): RegisteredClientRepository {
        val frontendClient = RegisteredClient.withId(UUID.nameUUIDFromBytes(("station-frontend-client").toByteArray()).toString())
            .clientId("station-frontend-client")
            // No client secret for public clients (SPA)
            .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
            // --- Allow BOTH Authorization Code (for potential future use) AND Password (ROPC) ---
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("http://localhost:5173/oidc-callback") // OIDC Callback URI
            .postLogoutRedirectUri("http://localhost:5173/login") // Post Logout URI
            .scope(OidcScopes.OPENID)
            .clientSettings(
                ClientSettings.builder()
                    .requireProofKey(true)
                    .build()
            )
            .build()

        return InMemoryRegisteredClientRepository(frontendClient)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    // This AuthenticationManager is used by the form login AND the ROPC grant type
    fun authenticationManager(userDetailsService: UserDetailsService, passwordEncoder: PasswordEncoder): AuthenticationManager {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder)
        return ProviderManager(provider)
    }

    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext>): JwtDecoder {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val rsaKey = RSAKey.Builder(applicationConfig.rsaPublicKey)
            .privateKey(applicationConfig.rsaPrivateKey)
            .keyID(UUID.randomUUID().toString())
            .build()
        val jwkSet = JWKSet(rsaKey)
        return JWKSource { jwkSelector: JWKSelector, _: SecurityContext? -> jwkSelector.select(jwkSet) }
    }

    @Bean
    fun jwtEncoder(jwkSource: JWKSource<SecurityContext>): NimbusJwtEncoder {
        return NimbusJwtEncoder(jwkSource)
    }

    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings {
        // Ensure issuer URI matches frontend config if OIDC features (/userinfo) are used
        return AuthorizationServerSettings.builder()
            .issuer("http://localhost:8081") // Use configured or default issuer
            .build()
    }


    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf("http://localhost:5173") // Frontend origin
            allowedMethods = listOf(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(), // Needed for /oauth2/token
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()
            )
            allowedHeaders = listOf("*") // Allow all headers
            allowCredentials = true // Important for potential session cookies (form login) and potentially future needs
            maxAge = 3600L
        }
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration) // Apply CORS to all paths
        return source
    }
}
