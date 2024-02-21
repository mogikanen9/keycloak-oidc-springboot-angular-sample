package com.example.heroes;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests(conf -> conf.anyRequest().authenticated())
				.sessionManagement(conf -> conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(CsrfConfigurer::disable).oauth2ResourceServer(conf -> conf.jwt(jwt -> { // Customizer.withDefaults()
					jwt.jwtAuthenticationConverter(new MyJwtAuthenticationConverter());
				})).build();
	}

	private static class MyJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

		private static final String SCOPE_AUTHORITIES_CLAIM = "scope";

		@Override
		public AbstractAuthenticationToken convert(Jwt jwt) {
			// Extract scopes from JWT token
			Collection<GrantedAuthority> authorities = this.extractAuthorities(jwt);

			// Create authentication token with extracted scopes
			return new JwtAuthenticationToken(jwt, authorities);
		}

		private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
			List<String> scopes = jwt.getClaimAsStringList(SCOPE_AUTHORITIES_CLAIM);
			if (scopes == null) {
				return Collections.emptyList();
			}
			// Convert scopes to authorities
			return scopes.stream().map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
					.collect(toList());
		}

	}
}
