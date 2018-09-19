package org.springframework.cloud.samples.authgateway;

import reactor.core.publisher.Mono;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

/**
 * @author Ryan Baxter
 */
public class RoleBasedServerLogoutSuccessHandler implements ServerLogoutSuccessHandler {

	private RedirectServerLogoutSuccessHandler redirectServerLogoutSuccessHandler = new RedirectServerLogoutSuccessHandler();

	@Override
	public Mono<Void> onLogoutSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
		ResponseCookie responseCookie = ResponseCookie.from("type", "").build();
		webFilterExchange.getExchange().getResponse().addCookie(responseCookie);
		return redirectServerLogoutSuccessHandler.onLogoutSuccess(webFilterExchange, authentication);
	}
}
