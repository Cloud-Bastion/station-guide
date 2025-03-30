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
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
        http.getConfigurer(OAuth2AuthorizationServerConfigurer::class.java)
            .oidc(Customizer.withDefaults()) // Enable OpenID Connect 1.0

        // Enable CORS for Authorization Server endpoints
        http.cors(Customizer.withDefaults())
            // Configure the authorization endpoint to require login
            // Redirect unauthenticated users to the login page
            .exceptionHandling { exceptions ->
                exceptions.authenticationEntryPoint(
                    LoginUrlAuthenticationEntryPoint("/login") // Default Spring login page path
                )
            }
            // Optional: Configure User Info endpoint if needed
            // .oidc(oidc -> oidc.userInfoEndpoint(Customizer.withDefaults()))

        // REMOVED: Don't configure the auth server itself as a resource server here,
        // as it might interfere with the /oauth2/token endpoint which doesn't expect a JWT.
        // http.oauth2ResourceServer { resourceServer ->
        //     resourceServer.jwt(Customizer.withDefaults())
        // }

        return http.build()
    }


    @Bean
    @Order(2) // Default security filter chain
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.cors(Customizer.withDefaults()) // Apply CORS globally
            .authorizeHttpRequests { auth ->
                // Allow access to the login page and error pages
                auth
                    .requestMatchers("/login", "/error").permitAll()
                    // Secure all other endpoints handled by this chain
                    .anyRequest().authenticated()
            }
            .csrf { csrf -> csrf.disable() } // Disable CSRF for stateless API (consider enabling for stateful parts like login form if not using separate frontend)
            .sessionManagement { session ->
                // Use session IF REQUIRED for the form login page
                 session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            }
            // Configure form login for the default Spring Security login page
            .formLogin(Customizer.withDefaults())

        return http.build()
    }


    @Bean
    fun registeredClientRepository(): RegisteredClientRepository {
        val frontendClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("station-frontend-client")
            // No client secret for public clients (SPA)
            .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) // Use Authorization Code Grant
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN) // Allow refresh tokens
            // Define the allowed redirect URI for the frontend app after login
            .redirectUri("http://localhost:5173/oidc-callback") // OIDC Callback URI (MUST match frontend config)
            // Define the allowed redirect URI after logout (optional but recommended)
            .postLogoutRedirectUri("http://localhost:5173/login") // Post Logout URI (MUST match frontend config)
            .scope(OidcScopes.OPENID) // Standard OIDC scope
            .scope(OidcScopes.PROFILE) // Scope for user profile information
            .scope("station.read") // Custom scope
            .scope("station.write") // Custom scope
            .clientSettings(
                ClientSettings.builder()
                    .requireProofKey(true) // Enable PKCE (Proof Key for Code Exchange) - REQUIRED for public clients using Auth Code flow
                    .requireAuthorizationConsent(true) // Optional: Set to true to show consent screen, false otherwise
                    .build()
            )
            .build()

        // If ROPC is needed for a *different* client (e.g., testing, mobile app), register it separately.
        // Example ROPC client (adjust as needed):
        /*
        val ropcClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("ropc-client")
            // .clientSecret(passwordEncoder().encode("ropc-secret")) // Use a secret for confidential ROPC clients
            // .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .clientAuthenticationMethod(ClientAuthenticationMethod.NONE) // Or NONE if it's a public ROPC client
            .authorizationGrantType(AuthorizationGrantType.PASSWORD) // ROPC Grant
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .scope("station.read")
            .scope("station.write")
            .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
            .build()
        */

        // return InMemoryRegisteredClientRepository(frontendClient, ropcClient) // Include both if needed
        return InMemoryRegisteredClientRepository(frontendClient) // Only frontend client for now
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    // This AuthenticationManager is used by the form login and potentially ROPC if enabled
    fun authenticationManager(userDetailsService: UserDetailsService, passwordEncoder: PasswordEncoder): AuthenticationManager {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder)
        return ProviderManager(provider)
    }

    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext>): JwtDecoder {
        // Decoder used by resource servers and potentially the auth server itself (e.g., for /userinfo)
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        // Generates the JWK set used for signing tokens
        val rsaKey = RSAKey.Builder(applicationConfig.rsaPublicKey)
            .privateKey(applicationConfig.rsaPrivateKey)
            .keyID(UUID.randomUUID().toString())
            .build()
        val jwkSet = JWKSet(rsaKey)
        return JWKSource { jwkSelector: JWKSelector, _: SecurityContext? -> jwkSelector.select(jwkSet) }
    }

    @Bean
    fun jwtEncoder(jwkSource: JWKSource<SecurityContext>): NimbusJwtEncoder {
        // Encoder used to create the JWTs issued by the auth server
        return NimbusJwtEncoder(jwkSource)
    }

    @Bean
    // Provides the authorization server settings, including issuer URL
    fun authorizationServerSettings(): AuthorizationServerSettings {
        // Configure the issuer URI properly. Replace 'http://localhost:9000' with your actual issuer URI if different.
        // It should match the 'authority' configured in the frontend oidcConfig.
        // You can also fetch this from application properties.
        return AuthorizationServerSettings.builder()
            .issuer("http://localhost:8081") // Use configured or default issuer
            .build()
    }


    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            // Allow requests from the frontend development server
            allowedOrigins = listOf("http://localhost:5173")
            allowedMethods = listOf(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()
            )
            // Allow all standard headers and custom ones like Authorization
            allowedHeaders = listOf("*")
            // Allow credentials (cookies, authorization headers) - needed for session cookie with form login
            allowCredentials = true
            // How long the results of a preflight request can be cached
            maxAge = 3600L
        }
        val source = UrlBasedCorsConfigurationSource()
        // Apply this CORS configuration to all paths
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
