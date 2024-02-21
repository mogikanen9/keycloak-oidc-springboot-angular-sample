package com.example.eumprotected.simplejobrestclient.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ClientCredentialsGrant;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.AccessToken;

@Component
public class MyHeroesService {

	private static final Logger log = LoggerFactory.getLogger(MyHeroesService.class);

	@Value("${rest.api.heroes.url}")
	private String heroesApiUrl;

	@Value("${myidp.keycloak.clientid}")
	private String clientId;

	@Value("${myidp.keycloak.clientsecret}")
	private String clientSecret;

	@Value("${myidp.keycloak.endpoint.token}")
	private String tokenEndpoint;

	private final RestTemplate restTemplate = new RestTemplate();

	public HeroProfile[] fetchData() throws ParseException, URISyntaxException, IOException {

		String accessToken = this.getAccessToken();

		HttpHeaders headers = new HttpHeaders();
		// Add headers as needed
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-Type", "application/json");

		// Create HttpEntity and pass HttpHeaders
		HttpEntity<String> entity = new HttpEntity<>(headers);

		// Make the HTTP request with RestTemplate
		ResponseEntity<HeroProfile[]> response = restTemplate.exchange(heroesApiUrl, HttpMethod.GET, entity,
				HeroProfile[].class);

		// Process the response
		return response.getBody();

	}

	protected String getAccessToken() throws URISyntaxException, ParseException, IOException {
		// Construct the client credentials grant
		AuthorizationGrant clientGrant = new ClientCredentialsGrant();

		// The credentials to authenticate the client at the token endpoint
		ClientID pClientID = new ClientID(this.clientId);
		Secret pClientSecret = new Secret(this.clientSecret);
		ClientAuthentication clientAuth = new ClientSecretBasic(pClientID, pClientSecret);

		// The request scope for the token (may be optional)
		Scope scope = new Scope("access_to_heroes_api");

		// The token endpoint
		URI tokenEndpoint = new URI(this.tokenEndpoint);

		// Make the token request
		TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, clientGrant, scope);

		TokenResponse response = TokenResponse.parse(request.toHTTPRequest().send());

		if (!response.indicatesSuccess()) {
			// We got an error response...
			TokenErrorResponse errorResponse = response.toErrorResponse();
			log.error("errorResponse", errorResponse.toJSONObject());

			throw new RuntimeException(errorResponse.toString());

		} else {
			AccessTokenResponse successResponse = response.toSuccessResponse();

			// Get the access token
			AccessToken accessToken = successResponse.getTokens().getAccessToken();

			return accessToken.getValue();
		}

	}

	public static record HeroProfile(int id, String name) {
	}
}
