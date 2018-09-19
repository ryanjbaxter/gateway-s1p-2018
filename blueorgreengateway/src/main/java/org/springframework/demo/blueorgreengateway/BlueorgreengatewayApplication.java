package org.springframework.demo.blueorgreengateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.filter.LoadBalancerClientFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootApplication
public class BlueorgreengatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlueorgreengatewayApplication.class, args);
	}

	@Bean
	public RouteLocator routeLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p.path("/blueorgreen").uri("lb://blueorgreen"))
				.route(p -> p.path("/frontend").filters(f -> f.setPath("/")).uri("lb://blueorgreenfrontend"))
				.route(p -> p.path("/frontend/**/*").filters(f -> f.stripPrefix(1)).uri("lb://blueorgreenfrontend"))
				.route(p -> p.path("/").or().path("/color").or().path("/js/**").uri("lb://blueorgreenfrontend"))
				.build();
	}

	@Bean
	public LoadBalancerClientFilter loadBalancerClientFilter(LoadBalancerClient client) {
		return new CustomLoadBalancerClientFilter(client);
	}

	@Configuration
	public class SecurityConfig {

		@Bean
		public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
			http.requestCache().disable().authorizeExchange().anyExchange().permitAll();
			return http.build();
		}
	}
}
