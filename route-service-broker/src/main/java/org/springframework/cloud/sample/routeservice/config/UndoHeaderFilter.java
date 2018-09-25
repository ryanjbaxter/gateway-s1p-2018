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

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebSession;

import reactor.core.publisher.Mono;

/**
 * Resets to the original {@link ServerWebExchange} but modifies the principal to
 * the currently logged in user.
 *
 * @author Rob Winch
 */
public class UndoHeaderFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerWebExchange original = exchange.getAttribute(HeaderFilter.ORIGINAL);
        if (original != null) {
            exchange = original.mutate()
                    .principal(exchange.getPrincipal())
                    .build();
        }
        //TODO test removing this
        return exchange.getSession()
                .flatMap(WebSession::save)
                .then(chain.filter(exchange));
    }
}
