package dev.aventix.station.authserver.config

import dev.aventix.station.authserver.user.OAuth2StationUserService
import dev.aventix.station.authserver.user.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
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
    private val userService: UserService,
    private val oauth2UserService: OAuth2StationUserService,
) {
    @Bean
    @Order(2)
    @Throws(Exception::class)
    fun authWebSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.cors(Customizer.withDefaults())
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/login", "/error", "/webjars/**", "/css/**", "/favicon.ico")
                    .permitAll() // Permit access to login page, error page, and static resources
                    .anyRequest().authenticated()
            } // All other requests require authentication
            // --- Custom Login Page Configuration ---
            .formLogin { formLogin: FormLoginConfigurer<HttpSecurity?> ->
                formLogin
                    .loginPage("/login")
            } // Point to your custom login page URL
            // --- OAuth2 Login Configuration (for Social Logins) ---
            .oauth2Login { oauth2Login: OAuth2LoginConfigurer<HttpSecurity?> ->
                oauth2Login
                    .loginPage("/login")
                    .userInfoEndpoint { it.userService(oauth2UserService) }
                // Optional: Add a custom success handler if you need logic AFTER authentication succeeds
                // .successHandler(yourCustomAuthenticationSuccessHandler())
            } // Also redirect to custom login page if social login fails or needs initiation
            // .defaultSuccessUrl("/logged-in-successfully", true) // Optional: Redirect after successful social login IF not part of an OAuth flow
            // --- Exception Handling for Unauthenticated Users ---
            // Redirect unauthenticated users trying to access protected resources (like /oauth2/authorize)
            // to the custom login page.
            .exceptionHandling { exceptions: ExceptionHandlingConfigurer<HttpSecurity?> ->
                exceptions
                    .authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login"))
            }

        http.headers {
            it.frameOptions { frameOptions ->
                frameOptions.sameOrigin()
            }.contentSecurityPolicy { csp ->
                csp.policyDirectives("frame-ancestors 'self' http://localhost:5173/;")
            }
        }

        return http.build()
    }

    // --- Spring Security Filter Chain for OAuth2 Authorization Server ---
    // This configures the OAuth2 endpoints (/oauth2/authorize, /oauth2/token, etc.)
    @Bean
    @Order(1)
    @Throws(Exception::class)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.with(OAuth2AuthorizationServerConfigurer.authorizationServer(), Customizer.withDefaults())

        // Explicitly define the matcher for Authorization Server endpoints
        val endpointsMatcher = http.getConfigurer(
            OAuth2AuthorizationServerConfigurer::class.java
        )
            .endpointsMatcher

        http.securityMatcher(endpointsMatcher)
            .authorizeHttpRequests { it.anyRequest().authenticated() }
            .csrf { it.ignoringRequestMatchers(endpointsMatcher) }

        // Enable OpenID Connect 1.0 features
        http.getConfigurer(OAuth2AuthorizationServerConfigurer::class.java)
            .oidc(Customizer.withDefaults())

        http.cors(Customizer.withDefaults())

        // Configure exception handling to redirect to the login page when authentication is required
        http.exceptionHandling { exceptions: ExceptionHandlingConfigurer<HttpSecurity?> ->
            exceptions
                .authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login"))
        }

        http.headers {
            it.frameOptions { frameOptions ->
                frameOptions.sameOrigin()
            }.contentSecurityPolicy { csp ->
                csp.policyDirectives("frame-ancestors 'self' http://localhost:5173/;")
            }
        }

        // Accept access tokens for User Info and/or Client Registration
        http.oauth2ResourceServer { resourceServer: OAuth2ResourceServerConfigurer<HttpSecurity?> ->
            resourceServer
                .jwt(Customizer.withDefaults())
        }

        return http.build()
    }
//
//    @Bean
//    fun configureOpenIdToken() = OAuth2TokenCustomizer<JwtEncodingContext> {
//        val account = it.getPrincipal<StationAuthentication>().account
//        it.claims.subject(account.id.toString())
//        it.claims.claim("email", account.email)
//        it.claims.claim("role", "role_user")
//        val authorization = it.get<OAuth2Authorization>(OAuth2Authorization::class.java.name)
//        if (authorization != null) {
//            // Using the authorization ID as the SID is a common approach
//            it.claims.claim("sid", authorization.id)
//        } else {
//            // Fallback or generate if no authorization context (shouldn't happen for ID token)
//            it.claims.claim("sid", UUID.randomUUID().toString())
//        }
//    }

    @Bean
    fun registeredClientRepository(): RegisteredClientRepository {
        val frontendClient =
            RegisteredClient.withId(UUID.nameUUIDFromBytes(("station-frontend").toByteArray()).toString())
                .clientId("station-frontend")
                // No client secret for public clients (SPA)
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                // --- Allow BOTH Authorization Code (for potential future use) AND Password (ROPC) ---
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                //.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:5173/silent-renew") // OIDC Callback URI
                .redirectUri("http://localhost:5173/callback") // OIDC Callback URI
                .postLogoutRedirectUri("http://localhost:5173/") // Post Logout URI
                .scope("openid")
                .scope("profile")
                .clientSettings(
                    ClientSettings.builder()
                        .requireProofKey(true)
                        .build()
                )
                .build()

        return InMemoryRegisteredClientRepository(frontendClient)
    }


//    @Bean
//    // This AuthenticationManager is used by the form login AND the ROPC grant type
//    fun authenticationManager(
//        userDetailsService: UserDetailsService,
//        passwordEncoder: PasswordEncoder,
//    ): AuthenticationManager {
//        val provider = DaoAuthenticationProvider()
//        provider.setUserDetailsService(userDetailsService)
//        provider.setPasswordEncoder(passwordEncoder)
//        return ProviderManager(provider)
//    }

    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings {
        // Ensure issuer URI matches frontend config if OIDC features (/userinfo) are used
        return AuthorizationServerSettings.builder()
            .issuer("http://localhost:8080") // Use configured or default issuer
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
