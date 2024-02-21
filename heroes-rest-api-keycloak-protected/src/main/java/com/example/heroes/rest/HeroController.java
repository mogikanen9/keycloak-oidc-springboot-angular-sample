package com.example.heroes.rest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class HeroController {

	@GetMapping("/heroes")
	public List<HeroProfile> listHeroes(Principal principal) throws ForbiddenException {

		log.info(String.format("Principal->%s", principal != null ? principal.getName() : "N/A"));

		// Get the current authentication from the security context
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// Check if the authentication object is an instance of JwtAuthenticationToken
		if (authentication instanceof JwtAuthenticationToken) {
			JwtAuthenticationToken jwtAuthentication = (JwtAuthenticationToken) authentication;

			// Extract authorities (scopes) from the authentication object
			Collection<GrantedAuthority> authorities = jwtAuthentication.getAuthorities();

			authorities.stream().forEach(ga -> {
				log.info(ga.toString());
			});

			// Check if the required scope is present
			if (authorities.stream()
					.anyMatch(authority -> authority.getAuthority().contains("access_to_heroes_api"))) {
				// The required scope is present, proceed with the request
				log.info("Authorized");
				List<HeroProfile> rs = new ArrayList<>();

				rs.add(new HeroProfile(1, "Wonder Woman"));

				rs.add(new HeroProfile(2, "Batman"));

				return rs;
			} else {
				// The required scope is not present, return an error or handle accordingly
				log.info("Unauthorized");
				throw new ForbiddenException("You are not authorized to access this resource");
			}
		} else {
			throw new RuntimeException("Internal error!");
		}

	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	public class ForbiddenException extends RuntimeException {
		public ForbiddenException(String message) {
			super(message);
		}
	}
}

record HeroProfile(int id, String name) {
}