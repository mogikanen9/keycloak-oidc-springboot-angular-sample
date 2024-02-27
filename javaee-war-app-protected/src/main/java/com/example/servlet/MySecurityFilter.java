package com.example.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;

@WebFilter(filterName = "DisplayInitParam", urlPatterns = { "/hello", "/oauth2/code/oidc" })
public class MySecurityFilter implements Filter {

	private static final String CLIENT_ID = "MyServletJavaEEApp";
	private static final String CLIENT_SECRET = "3F31PIifSPxWqlUA9Wjf6So5KXNprPBE";

	private static final String REDIRECT_URI = "http://localhost:8094/my-javaee-app-protected/oauth2/code/oidc";

	private static final String IAM_BASE_URI = "http://localhost:8080/realms/myrealm";

	private static final String AUTH_URL = IAM_BASE_URI + "/protocol/openid-connect/auth";
	private static final String TOKEN_URL = IAM_BASE_URI + "/protocol/openid-connect/token";
	// private static final String AUTH_SCOPE = "openid";

	private static final String AUTH_SCOPE = "openid profile roles access_to_heroes_api";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("custom filtering");

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		// 1 - check for auth in session
		UserAuthProfile profile = (UserAuthProfile) req.getSession().getAttribute("userAuthProfile");

		if (profile == null) {

			System.out.println("req.getRequestURI()->" + req.getRequestURI());

			// is this redirect back from IAM?
			if (req.getRequestURI().contains("/oauth2/code/oidc")) {
				// parse code request
				String authCode = req.getParameter("code");

				// get auth token
				try {
					AccessToken accessToken = getAccessToken(authCode);

					System.out.println("accessToken->" + accessToken);

					// Parse the token
					JWT jwt = JWTParser.parse(accessToken.getValue());

					// Retrieve the claims set
					JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();

					// Access individual claims
					String subject = claimsSet.getSubject();
					// You can access other claims similarly
					// For example:
					String issuer = claimsSet.getIssuer();
					Date expirationTime = claimsSet.getExpirationTime();

					Date now = new Date();
					if (now.after(expirationTime)) {
						throw new ServletException("Expired Token!");
					}

					Map<String, Object> realmAccess = claimsSet.getJSONObjectClaim("realm_access");
					List<String> roles = (List<String>) realmAccess.get("roles");

					Optional<String> clinicManagerRole = roles.stream().filter(r -> r.contains("clinic_manager"))
							.findFirst();
					if (clinicManagerRole.isEmpty()) {
						
						
				        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				        resp.getWriter().println("Access denied!");
						return;
					}

					String email = (String) claimsSet.getClaims().get("email");
					UserAuthProfile up = new UserAuthProfile(email, clinicManagerRole.get());
					req.getSession().setAttribute("userAuthProfile", up);
					System.out.println(up);

					chain.doFilter(request, response);

				} catch (ParseException | URISyntaxException | IOException | java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
					resp.getWriter().println("Smth went wrong inside!");
					return;
				}

			} else { // redirect to IAM

				String authRedirect = this.buildRedirect();
				System.out.println("authRedirect->" + authRedirect);
				resp.sendRedirect(authRedirect);
			}

		} else {
			chain.doFilter(request, response);
		}

	}

	protected String buildRedirect() throws UnsupportedEncodingException {
		StringBuilder strBuilder = new StringBuilder(AUTH_URL);
		strBuilder.append("?response_type=").append(URLEncoder.encode("code", StandardCharsets.UTF_8.name()))
				.append("&client_id=").append(URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8.name()))
				.append("&scope=").append(URLEncoder.encode(AUTH_SCOPE, StandardCharsets.UTF_8.name()))
				.append("&redirect_uri=").append(REDIRECT_URI).append("&nonce=").append(createNonce());
		return strBuilder.toString();
	}

	protected String createNonce() {
		return new BigInteger(50, new SecureRandom()).toString(16);
	}

	protected AccessToken getAccessToken(String authCode) throws URISyntaxException, ParseException, IOException {
		AuthorizationCode code = new AuthorizationCode(authCode);
		URI callback = new URI(REDIRECT_URI);

		AuthorizationCodeGrant codeGrant = new AuthorizationCodeGrant(code, callback);

		// The credentials to authenticate the client at the token endpoint
		ClientID clientID = new ClientID(CLIENT_ID);
		Secret clientSecret = new Secret(CLIENT_SECRET);
		ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret);

		// The token endpoint
		URI tokenEndpoint = new URI(TOKEN_URL);

		// Make the token request
		TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, codeGrant);

		TokenResponse response = TokenResponse.parse(request.toHTTPRequest().send());

		if (!response.indicatesSuccess()) {
			// We got an error response...
			TokenErrorResponse errorResponse = response.toErrorResponse();
			System.err.println("errorResponse->" + errorResponse);
			throw new IOException(errorResponse.toString());
		}

		AccessTokenResponse successResponse = response.toSuccessResponse();

		// Get the access token, the server may also return a refresh token
		AccessToken accessToken = successResponse.getTokens().getAccessToken();
		RefreshToken refreshToken = successResponse.getTokens().getRefreshToken();

		return accessToken;

	}

	public record UserAuthProfile(String username, String primaryRole) {
	};
}
