package com.example.webapp.eumprotected;

import java.util.Collection;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SecurityConfig {

	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
	
	@Bean
	public UserDetailsService userDetailsService() {
		return username -> {
			throw new UsernameNotFoundException("User not found");
		};
	}

	@Bean
	public OidcUserService oidcUserService() {
		return new OidcUserService() {
			@Override
			public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
				OidcUser delegateUser = super.loadUser(userRequest);
				// Customize the conversion from delegateUser to User if needed
				
				System.out.println("delegateUser.getUserInfo()->"+delegateUser.getUserInfo().toString());
				System.out.println("delegateUser family name->"+delegateUser.getUserInfo().getFamilyName());
				
				/*
				 * return new OidcUserImpl(delegateUser.getName(),
				 * delegateUser.getAuthorities(), delegateUser.getUserInfo());
				 */ 
				return delegateUser;
			}
		};
	}

	public static class OidcUserImpl implements OidcUser {

		private String username;
		private Collection<? extends GrantedAuthority> authorities;
		private OidcUserInfo userInfo;

		public OidcUserImpl(String username, Collection<? extends GrantedAuthority> authorities,
				OidcUserInfo userInfo) {
			super();
			this.username = username;
			this.authorities = authorities;
			this.userInfo = userInfo;
		}

		
		@Override
		public Map<String, Object> getAttributes() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return this.authorities;
		}

		@Override
		public String getName() {
			return this.username;
		}

		@Override
		public Map<String, Object> getClaims() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public OidcUserInfo getUserInfo() {
			return this.userInfo;
		}

		@Override
		public OidcIdToken getIdToken() {
			// TODO Auto-generated method stub
			return null;
		}
		// Implement the OidcUser interface methods
	}

}
