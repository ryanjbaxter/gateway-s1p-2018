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

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.handler.predicate.CloudFoundryRouteServiceRoutePredicateFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.sample.routeservice.filter.LoggingGatewayFilterFactory;
import org.springframework.cloud.sample.routeservice.filter.SubscriptionHandlerGatewayFilterFactory;
import org.springframework.cloud.sample.routeservice.servicebroker.RateLimiters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;

import java.util.function.Predicate;

import static org.springframework.cloud.gateway.handler.predicate.CloudFoundryRouteServiceRoutePredicateFactory.X_CF_FORWARDED_URL;

@Configuration
@AutoConfigureBefore(GatewayAutoConfiguration.class)
public class RouteConfiguration {

	@Bean
	public Predicate<ServerWebExchange> cloudFoundryPredicate() {
		return new CloudFoundryRouteServiceRoutePredicateFactory().apply(config -> {});
	}

	@Bean
	public GatewayFilter logger() {
		return new LoggingGatewayFilterFactory().apply(config -> {});
	}

	@Bean
	public RateLimiters rateLimiters() { return new RateLimiters();}

	@Bean
	public GatewayFilter subscriptionRateLimiter(KeyResolver resolver) {
		return new SubscriptionHandlerGatewayFilterFactory(rateLimiters(), resolver).apply(config -> {});
	}


	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder, KeyResolver resolver) {
		return builder.routes()
				.route(r -> r
						.path("/instanceId/{instanceId}")
						.and()
						.predicate(cloudFoundryPredicate())
						.filters(f -> { f
							.filter(logger())
							.filter(subscriptionRateLimiter(resolver))
							.requestHeaderToRequestUri(X_CF_FORWARDED_URL);
							return f;
						})
						.uri("no://op"))
				.build();
	}
}
