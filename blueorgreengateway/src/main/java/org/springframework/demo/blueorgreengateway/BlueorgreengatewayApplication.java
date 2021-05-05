package org.springframework.demo.blueorgreengateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@SpringBootApplication
@RestController
@LoadBalancerClient(name = "blueorgreen", configuration = LoadBalancerConfiguration.class)
public class BlueorgreengatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlueorgreengatewayApplication.class, args);
	}

	@Bean
	public RouteLocator routeLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p.path("/blueorgreen")
						.filters(this::circuitBreaker)
						.uri("lb://blueorgreen"))
				.route(p -> p.path("/").or().path("/color").or().path("/js/**").uri("lb://blueorgreenfrontend"))
				.build();
	}

	private UriSpec circuitBreaker(GatewayFilterSpec gatewayFilterSpec) {
		return gatewayFilterSpec.circuitBreaker(config -> config.setFallbackUri("forward:/colorfallback"));
	}

	@RequestMapping("/colorfallback")
	public Map<String, String> fallbackColor() {
		return Collections.singletonMap("id", "red");
	}
}

