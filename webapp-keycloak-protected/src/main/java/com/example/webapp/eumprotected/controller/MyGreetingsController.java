package com.example.webapp.eumprotected.controller;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyGreetingsController {

	@GetMapping("/")
	public String home(Principal principal,Model model) {
		model.addAttribute("message", "Hello, Thymeleaf!");
		 model.addAttribute("username", principal.getName());
		 model.addAttribute("userDetails", principal.toString());
		 
		 model.addAttribute("fullName", getUserLastName());
		
		return "home";
	}
	
	public String getUserLastName() {
        // Retrieve the authentication object from the SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the authentication object is OAuth2AuthenticationToken
        if (authentication != null && authentication instanceof OAuth2AuthenticationToken) {
            // Cast the authentication object to OAuth2AuthenticationToken
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

            // Retrieve the user details from the OAuth2AuthenticationToken
            OidcUser oidcUser = (OidcUser) oauthToken.getPrincipal();

            // Access the user's last name from the user details
            String lastName = oidcUser.getFamilyName();

            return lastName;
        } else {
            return "User information not available.";
        }
    }
   
}
