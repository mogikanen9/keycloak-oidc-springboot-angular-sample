package com.example.heroes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests(conf -> conf.anyRequest().authenticated())
				.sessionManagement(conf -> conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(CsrfConfigurer::disable)
				.oauth2ResourceServer(conf -> conf.jwt(jwt->{   //Customizer.withDefaults()
					JwtAuthenticationConverter conv  = new JwtAuthenticationConverter();
					conv.setPrincipalClaimName("preferred_username");
					jwt.jwtAuthenticationConverter(conv);
				}))  
				.build();
	}

}
