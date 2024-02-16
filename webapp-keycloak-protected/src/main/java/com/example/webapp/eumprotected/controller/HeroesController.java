package com.example.webapp.eumprotected.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class HeroesController {

	// private RestClient restClient;

	private RestTemplate restTemplate;
	private final OAuth2AuthorizedClientService authorizedClientService;

	public HeroesController(RestTemplate restTemplate, OAuth2AuthorizedClientService authorizedClientService) {
		this.restTemplate = restTemplate;
		this.authorizedClientService = authorizedClientService;
	}

	@GetMapping("/heroes")
	public String heroes(Principal principal, Model model) {

		/*
		 * this.restClient =
		 * RestClient.builder().baseUrl("http://localhost:8090/api/heroes")
		 * .requestInterceptor(new ClientHttpRequestInterceptor() {
		 * 
		 * @Override public ClientHttpResponse intercept(HttpRequest request, byte[]
		 * body, ClientHttpRequestExecution execution) throws IOException {
		 * 
		 * if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
		 * request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer "+
		 * "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJUcF90SWZhbk1qemVGV09mZ1otLWVjN0ROQk45V09rNWxqNXRYV25RSTlVIn0.eyJleHAiOjE3MDc3NzczOTUsImlhdCI6MTcwNzc3NzA5NSwianRpIjoiZmJlMmQ1OTQtMzI5Ni00NWQ0LTk5NmMtYTFiMzJmZTIwZWJhIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9teXJlYWxtIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImI3Y2I3MzQ3LTQ1NTktNDg3MS04ODIwLTBjODAwNDhlOWJjNCIsInR5cCI6IkJlYXJlciIsImF6cCI6Ik15TXZjV2VsY29tZUFwcCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLW15cmVhbG0iLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiaGVyb2VzLXJvbGUiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBlbWFpbCBwcm9maWxlIiwiY2xpZW50SG9zdCI6IjE3Mi4xNy4wLjEiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6InNlcnZpY2UtYWNjb3VudC1teW12Y3dlbGNvbWVhcHAiLCJjbGllbnRBZGRyZXNzIjoiMTcyLjE3LjAuMSIsImNsaWVudF9pZCI6Ik15TXZjV2VsY29tZUFwcCJ9.WyZqYgqYr-OXEYeK4IDMLrmOg_pT3HeTPd-g6t7j7Xru3Vi4xfX69s5Nx0Hwbbs197Me7KkCIYK8l563zVJUBeJZe3pgI1H5MxuVx9RH0QzBhSKY9QoqVPIUmHmFppwQdJcNcq8ETN_gRm0aeqnWNAN1x7oRC2kH_cc-BpVu9KsMQClJmF-4MletsCe5PCnVx-Q5AIcuPO4VKzwvuSeEfChBBKltYXxD0wwXx2y-YctKd1witgtZ6uLqGpr4K85NYKQt98U60C2BPQt7-vixsZXRbiOgxQc08muGjIJjRjkLotJ9FfQFHMhHj6uunuVJcUkxc4RJEFqLFC3daCj0EA"
		 * ); }
		 * 
		 * return execution.execute(request, body); }
		 * 
		 * }).build();
		 */

		Optional<String> accessToken = getAccessToken();
		if (accessToken.isPresent()) {
			// Now you have the access token, you can use it as needed
			String tokenValue = accessToken.get();
			System.out.println("tokenValue->" + tokenValue);

			// Use the access token...
			String url = "http://localhost:8090/api/heroes";

			// Create HttpHeaders and set authorization header
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + tokenValue);
			// Create HttpEntity with headers
			HttpEntity<String> entity = new HttpEntity<>(headers);

			// Make a request with the HttpEntity containing the authorization header
			ResponseEntity<HeroProfile[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity,
					HeroProfile[].class);

			// Process the response
			// String responseBody = responseEntity.getBody();
			System.out.println("responseBody->" + responseEntity.getBody());
			model.addAttribute("items", Arrays.asList(responseEntity.getBody()));
		}

		else {
			model.addAttribute("items", Arrays.asList(new HeroProfile(1, "koko"), new HeroProfile(2, "jambo")));
		}

		return "heroes";
	}

	private Optional<String> getAccessToken() {

		String tokenValue = null;

		// Get the authentication object from SecurityContext
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// Check if the authentication object is not null and is an OAuth2
		// authentication
		if (authentication != null && authentication instanceof OAuth2AuthenticationToken) {
			// Cast the authentication object to OAuth2AuthenticationToken
			OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

			// Retrieve the client registration ID
			String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();

			// Retrieve the user's OAuth2 authorized client
			OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(clientRegistrationId,
					oauthToken.getName());

			// Get the access token from the authorized client
			OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

			// Now you have the access token, you can use it as needed
			tokenValue = accessToken.getTokenValue();
		}

		return Optional.ofNullable(tokenValue);
	}

}

record HeroProfile(int id, String name) {
}