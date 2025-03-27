package dev.aventix.station.authserver.provider.google

import com.nimbusds.jose.shaded.gson.Gson
import com.nimbusds.jose.shaded.gson.JsonObject
import dev.aventix.station.authserver.config.ApplicationConfigProperties
import dev.aventix.station.authserver.provider.AuthProviderDetails
import dev.aventix.station.authserver.provider.AuthProviderDetailsService
import dev.aventix.station.authserver.user.UserDto
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.util.*


@Service
class GoogleProfileDetailsService(
    private val serverProperties: ApplicationConfigProperties,
): AuthProviderDetailsService {

    override fun getUserDetails(accessToken: String): AuthProviderDetails {
        val restTemplate = RestTemplate()
        val httpHeaders = HttpHeaders()
        httpHeaders.setBearerAuth(accessToken)

        val requestEntity = HttpEntity<String>(httpHeaders)

        val url = "https://www.googleapis.com/oauth2/v2/userinfo"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity,
            String::class.java
        )
        val jsonObject = Gson().fromJson(
            response.body,
            JsonObject::class.java
        )

        val email = jsonObject["email"].toString().replace("\"", "")
//        user.setEmail(jsonObject["email"].toString().replace("\"", ""))
//        user.setFirstName(jsonObject["name"].toString().replace("\"", ""))
//        user.setLastName(jsonObject["given_name"].toString().replace("\"", ""))
//        user.setPassword(UUID.randomUUID().toString())
        return AuthProviderDetails(email)
    }

    fun oAuthAccessToken(code: String): String {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val params = LinkedMultiValueMap<String, String>().apply {
            add("code", code);
            add("redirect_uri", "http://localhost:8080/grantcode");
            add("client_id", serverProperties.authProviders.google.clientId);
            add("client_secret", serverProperties.authProviders.google.clientSecret);
            add("scope", "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile");
            add("scope", "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email");
            add("scope", "openid");
            add("grant_type", "authorization_code");
        }
        val requestEntity = HttpEntity<MultiValueMap<String, String>>(params, headers)
        val url = "https://oauth2.googleapis.com/token"
        val response = restTemplate.postForObject(url, requestEntity, String::class.java)
        val jsonObject = Gson().fromJson(response, JsonObject::class.java)
        return jsonObject.get("access_token").asString.replace("\"", "")
    }
}