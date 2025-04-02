package dev.aventix.station.authserver.user.session

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import dev.aventix.station.authserver.user.UserRepository
import dev.aventix.station.authserver.user.spring.StationAuthentication
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.jackson2.SecurityJackson2Modules
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.OAuth2RefreshToken
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class OAuth2AuthorizationService(
    private val accountRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val sessionTokenRepository: SessionTokenRepository,
    private val registeredClientRepository: RegisteredClientRepository
) : OAuth2AuthorizationService {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val authMapper = ObjectMapper().apply {
        registerModules(SecurityJackson2Modules.getModules(this@OAuth2AuthorizationService.javaClass.classLoader))
        registerModule(OAuth2AuthorizationServerJackson2Module())
    }

    override fun save(authorization: OAuth2Authorization) {
        logger.debug("Save authorization: {} {}", authorization.id, authorization.accessToken)
        sessionRepository.save(authorization.toSession())
    }

    override fun remove(authorization: OAuth2Authorization) {
        logger.debug("Remove authorization: {}", authorization.id)
        sessionRepository.deleteById(UUID.fromString(authorization.id))
    }

    override fun findById(id: String): OAuth2Authorization? {
        logger.debug("Find authorization by id {}...", id)
        return sessionRepository
            .findByIdOrNull(UUID.fromString(id))
            ?.toOAuth2Authorization()
            .also { logger.debug("Found authorization {}", it) }
    }

    override fun findByToken(token: String, tokenType: OAuth2TokenType?) = when (tokenType?.value) {
        OAuth2ParameterNames.STATE -> sessionRepository.findByState(token)
        OAuth2ParameterNames.CODE -> sessionRepository.findByStateOrToken(token)
        OAuth2ParameterNames.ACCESS_TOKEN -> sessionTokenRepository.findByAccessToken(token)
        OAuth2ParameterNames.REFRESH_TOKEN -> sessionTokenRepository.findByRefreshToken(token)
        OidcParameterNames.ID_TOKEN -> sessionTokenRepository.findByIdToken(token)
        null -> sessionRepository.findByStateOrToken(token)
        else -> null
    }?.toOAuth2Authorization().also { logger.debug("Found token {}", it) }

    private fun Session.toOAuth2Authorization() = OAuth2Authorization
        .withRegisteredClient(registeredClientRepository.findById(clientId))
        .fromSession(this)
        .build()

    private fun OAuth2Authorization.toSession() = let {
        Session().apply {
            id = UUID.fromString(it.id)
            clientId = it.registeredClientId
            principalName = it.principalName
            grantType = it.authorizationGrantType.value
            scopes = it.authorizedScopes.toSet()
            attributes = authMapper.writeValueAsString(it.attributes.toMutableMap().also { attr ->
                attr.remove("java.security.Principal")
            })
            state = it.attributes[OAuth2ParameterNames.STATE] as String?
            tokens = listOfNotNull(
                it.getToken(OAuth2AuthorizationCode::class.java)?.toSessionToken(),
                it.getToken(OAuth2AccessToken::class.java)?.toSessionToken(),
                it.getToken(OAuth2RefreshToken::class.java)?.toSessionToken(),
                it.getToken(OidcIdToken::class.java)?.toSessionToken(),
            ).toMutableList()
            tokens.forEach { token -> token.session = this }
            account = accountRepository.findByEmail(principalName!!).getOrNull() ?: error("Account not found")
        }
    }

    private fun OAuth2Authorization.Builder.fromSession(session: Session) = session.run {
        this@fromSession
            .id(id.toString())
            .principalName(principalName)
            .authorizationGrantType(findGrantType(grantType))
            .authorizedScopes(scopes)
            .attributes {
                it.putAll(
                    authMapper.readValue(
                        attributes,
                        object : TypeReference<Map<String, Any>>() {})
                )
                it["java.security.Principal"] = StationAuthentication(account)
                if (state != null) {
                    it[OAuth2ParameterNames.STATE] = state
                }
            }
            .apply {
                tokens.forEach {
                    val (token, updatedMetadata) = it.writeToOAuth2Token()
                    token(token) { metadata ->
                        metadata.putAll(updatedMetadata)
                    }
                }
            }
        this@fromSession
    }
}