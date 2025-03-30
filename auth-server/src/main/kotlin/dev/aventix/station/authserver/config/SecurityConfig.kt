package dev.aventix.station.authserver.config

import com.nimbusds.jose.jwk.JWKSelector
import com.nimbusds.jose.jwk.JWKSet
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
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.RequestMatcher
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
        // http.exceptionHandling { exceptions ->
        //     exceptions.defaultAuthenticationEntryPointFor(
        //         LoginUrlAuthenticationEntryPoint("/login"), // Adjust if your login page URL is different
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
                // Allow access to the custom login endpoint and OAuth2 endpoints
                auth.requestMatchers("/api/v1/auth/login", "/oauth2/**", "/login").permitAll()
                // Secure other endpoints if necessary, or permit all for now
                auth.anyRequest().authenticated() // Or permitAll() depending on needs
            }
            .csrf { csrf -> csrf.disable() } // Disable CSRF for stateless API
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            // Configure form login if the default Spring Security login page is used
            .formLogin(Customizer.withDefaults()) // Use default login page for auth server

        return http.build()
    }


    @Bean
    fun registeredClientRepository(): RegisteredClientRepository {
        val frontendClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("station-frontend-client")
            // No client secret for public clients using PKCE
            .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN) // Optional: Add refresh token grant
            .redirectUri("http://localhost:5173/oauth/callback") // Frontend callback URL
            // .postLogoutRedirectUri("http://localhost:5173/") // Optional: Where to redirect after logout
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .scope("station.read") // Example custom scope
            .scope("station.write") // Example custom scope
            .clientSettings(
                ClientSettings.builder()
                    .requireProofKey(true) // Enable PKCE
                    .requireAuthorizationConsent(false) // Set to true to show consent screen
                    .build()
            )
            .build()

        // Keep other clients if needed, e.g., for backend services
        // val oidcClient = RegisteredClient.withId(UUID.randomUUID().toString()).clientId("client-id")
        //     .clientSecret("{noop}client-secret") // Use passwordEncoder for secrets
        //     .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        //     .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        //     .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
        //     .redirectUri("http://127.0.0.1:8080/login/oauth2/code/oidc-client") // Example backend callback
        //     .postLogoutRedirectUri("http://127.0.0.1:8080/")
        //     .scope(OidcScopes.OPENID).scope(OidcScopes.PROFILE)
        //     .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
        //     .build()

        return InMemoryRegisteredClientRepository(frontendClient /*, oidcClient */)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(userDetailsService: UserDetailsService, passwordEncoder: PasswordEncoder): AuthenticationManager {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder) // Set the password encoder
        return ProviderManager(provider)
    }

    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext>): JwtDecoder {
        // Use the JWKSource bean provided by Spring Authorization Server
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        // Use the keys defined in application properties
        val rsaKey = RSAKey.Builder(applicationConfig.rsaPublicKey)
            .privateKey(applicationConfig.rsaPrivateKey)
            .keyID(UUID.randomUUID().toString()) // Generate a key ID
            .build()
        val jwkSet = JWKSet(rsaKey)
        return JWKSource { jwkSelector: JWKSelector, _: SecurityContext? -> jwkSelector.select(jwkSet) }
    }

    @Bean
    fun jwtEncoder(jwkSource: JWKSource<SecurityContext>): NimbusJwtEncoder {
        // Use the same JWKSource for encoding
        return NimbusJwtEncoder(jwkSource)
    }

    // Define CORS configuration globally
    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            // Allow the frontend origin
            allowedOrigins = listOf("http://localhost:5173")
            // Allow common methods and OAuth2 methods
            allowedMethods = listOf(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()
            )
            // Allow all headers, including Authorization
            allowedHeaders = listOf("*")
            // Allow credentials (cookies, authorization headers)
            allowCredentials = true
            // Max age for preflight requests
            maxAge = 3600L
        }
        val source = UrlBasedCorsConfigurationSource()
        // Apply CORS configuration to all paths
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
