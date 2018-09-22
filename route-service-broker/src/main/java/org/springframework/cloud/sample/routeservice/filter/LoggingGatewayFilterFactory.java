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

package org.springframework.cloud.sample.routeservice.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern.PathMatchInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.springframework.cloud.gateway.handler.predicate.CloudFoundryRouteServiceRoutePredicateFactory.X_CF_FORWARDED_URL;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.URI_TEMPLATE_VARIABLES_ATTRIBUTE;

@Component
public class LoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {
	private final Logger log = LoggerFactory.getLogger(LoggingGatewayFilterFactory.class);

	@Override
	public GatewayFilter apply(Object config) {
		return (exchange, chain) -> {

			ServerHttpRequest request = exchange.getRequest();

			String serviceInstanceId = getServiceInstanceId(exchange);

			URI forwardedUrl = getForwardedUrl(request);

			log.info("Forwarding request: serviceInstanceId={}, method={}, headers={}, url={}",
					serviceInstanceId,
					request.getMethod(),
					request.getHeaders(),
					forwardedUrl);

			return chain.filter(exchange)
					.doOnSuccess(x -> log.info("Response: serviceInstanceId={}, method={}, headers={}, url={}",
							serviceInstanceId,
							request.getMethod(),
							request.getHeaders(),
							forwardedUrl))
					.doOnError(e -> log.error("Error: exception={}, serviceInstanceId={}, method={}, headers={}, url={}",
							e,
							serviceInstanceId,
							request.getMethod(),
							request.getHeaders(),
							forwardedUrl));
		};
	}

	private String getServiceInstanceId(ServerWebExchange exchange) {
		PathMatchInfo uriVariablesAttr = exchange.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		Map<String, String> uriVariables = uriVariablesAttr.getUriVariables();
		return uriVariables.get("instanceId");
	}

	private URI getForwardedUrl(ServerHttpRequest request) {
		List<String> headers = request.getHeaders().get(X_CF_FORWARDED_URL);
		if (headers == null || headers.isEmpty()) {
			log.warn("No " + X_CF_FORWARDED_URL + " header in request");
			return null;
		}

		String forwardedUrl = headers.get(0);
		try {
			return new URI(forwardedUrl);
		} catch (URISyntaxException e) {
			log.warn("Invalid value for " + X_CF_FORWARDED_URL + " header: " + forwardedUrl);
			return null;
		}
	}
}