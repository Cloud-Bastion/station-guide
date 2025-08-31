package dev.aventix.station.authserver.token

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.stereotype.Component
import java.util.stream.Collectors

@Component
class AuthTokenCustomizer: OAuth2TokenCustomizer<JwtEncodingContext> {

    override fun customize(context: JwtEncodingContext) {
        val principal: Authentication = context.getPrincipal() // Get the authenticated principal

        // Check if the principal is your custom user type
        // if ("access_token".equals(context.getTokenType().getValue()) && principal.getPrincipal() instanceof LocalAppUser) {
        //     LocalAppUser localUser = (LocalAppUser) principal.getPrincipal();

        //     // Add custom claims based on your local user
        //     context.getClaims().claim("user_id", localUser.getId());
        //     context.getClaims().claim("email", localUser.getEmail());
        //     // Example: Add roles/authorities from your local user model
        //     Set<String> authorities = localUser.getAuthorities().stream()
        //                                    .map(GrantedAuthority::getAuthority)
        //                                    .collect(Collectors.toSet());
        //     context.getClaims().claim("authorities", authorities);

        //     System.out.println("Customizing token for local user: " + localUser.getEmail());
        // }
        // --- Placeholder until LocalAppUser is used ---
        if ("access_token" == context.tokenType.value) {
            System.out.println("Customizing token for principal: " + principal.getName())
            val authorities: Set<String> = principal.getAuthorities().stream()
                .map { obj: GrantedAuthority -> obj.authority }
                .collect(Collectors.toSet())
            context.claims.claim("authorities", authorities) // Add existing authorities
            // Add other claims available from the current principal if needed
            if (principal is OAuth2User) {
                val oAuth2User = principal as OAuth2User
                context.claims.claim("full_name", oAuth2User.getAttribute("name"))
                context.claims.claim("picture", oAuth2User.getAttribute("picture"))
            }
        }
        // --- End Placeholder ---
    }

}