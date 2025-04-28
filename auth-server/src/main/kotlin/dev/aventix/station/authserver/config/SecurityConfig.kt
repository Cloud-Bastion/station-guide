package dev.aventix.station.authserver.config

import dev.aventix.station.authserver.user.StationOidcUserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
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
        http.cors {
            it.configurationSource(UrlBasedCorsConfigurationSource().apply {
                registerCorsConfiguration("/**", CorsConfiguration().apply {
                    allowedOriginPatterns = listOf("https://*.cakmak-station.de", "http://localhost:*")
                    allowedHeaders = listOf("*")
                    allowedMethods = listOf("*")
                    allowCredentials = true
                    maxAge = 3600
                })
            })
        }.authorizeHttpRequests { authorize ->
            authorize.requestMatchers("/login", "/error", "/webjars/**", "/css/**", "/favicon.ico").permitAll()
                .anyRequest().authenticated()
        }.formLogin { formLogin: FormLoginConfigurer<HttpSecurity?> ->
            formLogin.loginPage("/login")
        }.oauth2Login { oauth2Login: OAuth2LoginConfigurer<HttpSecurity?> ->
            oauth2Login.loginPage("/login").userInfoEndpoint { it.oidcUserService(oidcUserService) }
        }.exceptionHandling { exceptions: ExceptionHandlingConfigurer<HttpSecurity?> ->
            exceptions.authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login"))
        }
        http
            .oauth2ResourceServer { oauth2: OAuth2ResourceServerConfigurer<HttpSecurity?> ->
                oauth2
                    .jwt(Customizer.withDefaults())
            }
        http.csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }

        http.headers {
            it.frameOptions { frameOptions ->
                frameOptions.sameOrigin()
            }.contentSecurityPolicy { csp ->
                csp.policyDirectives("frame-ancestors 'self' http://localhost:5173/;")
                csp.policyDirectives("frame-ancestors 'self' https://dashboard.cakmak-station.de/;")
            }
        }

        return http.build()
    }

    @Bean
    @Order(1)
    @Throws(Exception::class)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.with(OAuth2AuthorizationServerConfigurer.authorizationServer(), Customizer.withDefaults())
        val endpointsMatcher = http.getConfigurer(
            OAuth2AuthorizationServerConfigurer::class.java
        ).endpointsMatcher

        http.securityMatcher(endpointsMatcher).authorizeHttpRequests { it.anyRequest().authenticated() }

        http.csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }

        http.getConfigurer(OAuth2AuthorizationServerConfigurer::class.java).oidc(Customizer.withDefaults())
        http.cors {
            it.configurationSource(UrlBasedCorsConfigurationSource().apply {
                registerCorsConfiguration("/**", CorsConfiguration().apply {
                    allowedOriginPatterns = listOf("https://*.cakmak-station.de", "http://localhost:*")
                    allowedHeaders = listOf("*")
                    allowedMethods = listOf("*")
                    allowCredentials = true
                })
            })
        }
        http.exceptionHandling { exceptions: ExceptionHandlingConfigurer<HttpSecurity?> ->
            exceptions.authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login"))
        }

        http.headers {
            it.frameOptions { frameOptions ->
                frameOptions.sameOrigin()
            }.contentSecurityPolicy { csp ->
                csp.policyDirectives("frame-ancestors 'self' http://localhost:5173/;")
                csp.policyDirectives("frame-ancestors 'self' https://dashboard.cakmak-station.de/;")
            }
        }

        http.oauth2ResourceServer { resourceServer: OAuth2ResourceServerConfigurer<HttpSecurity?> ->
            resourceServer.jwt(Customizer.withDefaults())
        }

        return http.build()
    }

    @Bean
    fun registeredClientRepository(): RegisteredClientRepository {
        val frontendClient =
            RegisteredClient.withId(UUID.nameUUIDFromBytes(("station-frontend").toByteArray()).toString())
                .clientId("station-frontend").clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("https://dashboard.cakmak-station.de/callbackw")
                .redirectUri("https://localhost:5173/callback")
                .redirectUri("https://dashboard.cakmak-station.de/silent-renew")
                .redirectUri("http://localhost:5173/silent-renew")
                .postLogoutRedirectUri("http://localhost:5173/")
                .postLogoutRedirectUri("https://dashboard.cakmak-station.de/")
                .scope("openid").scope("profile").scope("email")
                .scope("user:create")
                .clientSettings(
                    ClientSettings.builder().requireProofKey(true).build()
                ).build()

        return InMemoryRegisteredClientRepository(frontendClient)
    }

    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings {
        return AuthorizationServerSettings.builder().issuer("https://auth.cakmak-station.de").build()
    }
}
