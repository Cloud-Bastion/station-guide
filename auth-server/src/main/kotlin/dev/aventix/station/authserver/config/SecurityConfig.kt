package dev.aventix.station.authserver.config

import dev.aventix.station.authserver.user.StationOidcUserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
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
    private val oidcUserService: StationOidcUserService,
) {
    @Bean
    @Order(2)
    @Throws(Exception::class)
    fun authWebSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.cors(Customizer.withDefaults())
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/login", "/error", "/webjars/**", "/css/**", "/favicon.ico")
                    .permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin { formLogin: FormLoginConfigurer<HttpSecurity?> ->
                formLogin
                    .loginPage("/login")
            }
            .oauth2Login { oauth2Login: OAuth2LoginConfigurer<HttpSecurity?> ->
                oauth2Login
                    .loginPage("/login")
                    .userInfoEndpoint { it.oidcUserService(oidcUserService) }
            }
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
                .scope("email")
                .clientSettings(
                    ClientSettings.builder()
                        .requireProofKey(true)
                        .build()
                )
                .build()

        return InMemoryRegisteredClientRepository(frontendClient)
    }

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
