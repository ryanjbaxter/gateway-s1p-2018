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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static org.springframework.cloud.gateway.handler.predicate.CloudFoundryRouteServiceRoutePredicateFactory.X_CF_FORWARDED_URL;


/**
 * Cloud foundry routes to a different URL than the actual request being processed. The
 * request being processed is stored at X_CF_FORWARDED_URL. Since we want Spring Security
 * to act like it is within the application, this filter mutates the
 * {@link ServerWebExchange} to appear as though it is running within the application.
 * After Spring Security is done with the request the {@link UndoHeaderFilter} resets
 * the original request but with current principal.
 *
 * @author Rob Winch
 */
public class HeaderFilter implements WebFilter {
    private final Logger log = LoggerFactory.getLogger(HeaderFilter.class);

    public static final String ORIGINAL = "ORIGINAL_EXCHANGE";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getHeaders().getFirst(X_CF_FORWARDED_URL);
        log.info("Url: {}", url);
        if (url == null) {
            return chain.filter(exchange);
        }
        String path = UriComponentsBuilder.fromHttpUrl(url).build().getPath();
        log.info("Path: {}", path);
        ServerHttpRequest modified = request.mutate()
                .path(path)
                .build();
        ServerWebExchange modifiedExchange = exchange.mutate().request(modified).build();
        modifiedExchange.getAttributes().put(ORIGINAL, exchange);
        return chain.filter(modifiedExchange);
    }
}
