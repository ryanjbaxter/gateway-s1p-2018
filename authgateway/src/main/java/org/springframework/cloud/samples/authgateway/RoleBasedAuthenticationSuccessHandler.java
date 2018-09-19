package org.springframework.cloud.samples.authgateway;

import reactor.core.publisher.Mono;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;

/**
 * @author Ryan Baxter
 */
public class RoleBasedAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

	private RedirectServerAuthenticationSuccessHandler redirectServerAuthenticationSuccessHandler =
			new RedirectServerAuthenticationSuccessHandler();

	@Override
	public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
		if(authentication.getAuthorities().stream().anyMatch(a -> "role_premium".equalsIgnoreCase(a.getAuthority()))) {
			ResponseCookie cookie = ResponseCookie.from("type", "premium").path("/").build();
			webFilterExchange.getExchange().getResponse().addCookie(cookie);
		}
		return redirectServerAuthenticationSuccessHandler.onAuthenticationSuccess(webFilterExchange, authentication);
	}
}
