package dev.aventix.station.authserver.config

import com.nimbusds.jose.jwk.JWKSelector
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey // Import RSAKey
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
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
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.web.SecurityFilterChain
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
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
        http.getConfigurer(OAuth2AuthorizationServerConfigurer::class.java)
            .oidc(Customizer.withDefaults()) // Enable OpenID Connect 1.0

        // Accept access tokens for User Info and/or Client Registration
        http.oauth2ResourceServer { resourceServer ->
            resourceServer.jwt(Customizer.withDefaults())
        }

        // Redirect to the login page when not authenticated from the authorization endpoint
        // This might not be needed if you only use ROPC, but keep it if you might add other flows later
        // http.exceptionHandling { exceptions ->
        //     exceptions.defaultAuthenticationEntryPointFor(
        //         LoginUrlAuthenticationEntryPoint("/login"), // Default Spring Security login page
        //         http.getConfigurer(OAuth2AuthorizationServerConfigurer::class.java).authorizationEndpointMatcher
        //     )
        // }

        // Enable CORS for Authorization Server endpoints
        http.cors(Customizer.withDefaults())

        return http.build()
    }


    @Bean
    @Order(2) // Default security filter chain
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.cors(Customizer.withDefaults()) // Apply CORS globally
            .authorizeHttpRequests { auth ->
                // Allow access to the token endpoint and potentially other public endpoints
                // Explicitly permit /oauth2/token, /login (for auth server's own login page if needed),
                // and any public registration endpoints.
                auth.requestMatchers("/oauth2/token", "/login", "/api/v1/auth/register").permitAll()
                // Secure other endpoints by requiring authentication
                auth.anyRequest().authenticated() // Change back from permitAll()
            }
            .csrf { csrf -> csrf.disable() } // Disable CSRF for stateless API
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            // Configure form login for the default Spring Security login page (used by Auth Server itself if needed)
            .formLogin(Customizer.withDefaults())

        return http.build()
    }


    @Bean
    fun registeredClientRepository(): RegisteredClientRepository {
        val frontendClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("station-frontend-client")
            // No client secret for public clients (common for SPAs, even with ROPC)
            .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
            .authorizationGrantType(AuthorizationGrantType.PASSWORD) // Enable Password Grant
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN) // Keep refresh token grant
            // No redirectUri needed for ROPC
            .scope(OidcScopes.OPENID) // Keep standard scopes
            .scope(OidcScopes.PROFILE)
            .scope("station.read") // Keep custom scopes
            .scope("station.write")
            .clientSettings(
                ClientSettings.builder()
                    .requireProofKey(false) // Disable PKCE for ROPC
                    .requireAuthorizationConsent(false) // No consent screen for ROPC
                    .build()
            )
            .build()

        // Remove or comment out other clients if not needed
        // val oidcClient = ...

        return InMemoryRegisteredClientRepository(frontendClient)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    // This AuthenticationManager is crucial for the Password Grant
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
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf("http://localhost:5173") // Allow frontend origin
            allowedMethods = listOf(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()
            )
            allowedHeaders = listOf("*") // Allow all headers, including Authorization and Content-Type
            allowCredentials = true // Important for sending credentials if needed (though not typical for public ROPC)
            maxAge = 3600L
        }
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration) // Apply CORS to all paths
        return source
    }
}
