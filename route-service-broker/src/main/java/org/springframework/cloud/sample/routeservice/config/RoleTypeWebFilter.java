/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.sample.routeservice.config;

import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Communicates to end applications what type of user is requesting the application via
 * a cookie named type.
 *
 * @author Rob Winch
 */
public class RoleTypeWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // @formatter:off
//        firewall(exchange);
        return exchange.getPrincipal()
                .cast(Authentication.class)
                .map(Authentication::getAuthorities)
                .filter(this::isPremium)
                .map(a -> withType("premium", exchange))
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
        // @formatter:on
    }

    private void firewall(ServerWebExchange exchange) {
        HttpCookie typeCookie = exchange.getRequest()
                .getCookies()
                .getFirst("type");
        if (typeCookie != null) {
            throw new IllegalStateException("Malicious client tried to include Cookie named type");
        }
    }

    private boolean isPremium(Collection<? extends GrantedAuthority> authorities) {
        // @formatter:off
        return authorities
                .stream()
                .anyMatch(a -> "ROLE_PREMIUM".equalsIgnoreCase(a.getAuthority()));
        // @formatter:on
    }

    private ServerWebExchange withType(String role, ServerWebExchange exchange) {
        // @formatter:off
        return exchange.mutate()
                .request(r -> r.headers(withTypeCookie(role)))
                .build();
        // @formatter:on
    }

    private Consumer<HttpHeaders> withTypeCookie(String role) {
        return h -> {
            String cookieValue = h.getFirst(HttpHeaders.COOKIE);
            if (cookieValue == null) {
                cookieValue = "type=" + role;
            } else {
                cookieValue += " ; type=" + role;
            }
            h.set(HttpHeaders.COOKIE, cookieValue);
        };
    }
}
