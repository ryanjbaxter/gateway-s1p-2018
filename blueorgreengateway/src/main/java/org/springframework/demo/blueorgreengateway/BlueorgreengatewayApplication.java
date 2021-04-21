package org.springframework.demo.blueorgreengateway;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
						.filters(f -> f.circuitBreaker(c -> c.setFallbackUri("forward:/colorfallback")))
						.uri("lb://blueorgreen"))
				.route(p -> p.path("/").or().path("/color").or().path("/js/**").uri("lb://blueorgreenfrontend"))
				.build();
	}

	@Configuration
	public class SecurityConfig {

		@Bean
		public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
			http.requestCache().disable().authorizeExchange().anyExchange().permitAll();
			return http.build();
		}
	}

	@RequestMapping("/colorfallback")
	public Map<String, String> fallbackColor() {
		Map<String, String> map = new HashMap<>();
		map.put("id", "red");
		return map;
	}
}

class LoadBalancerConfiguration {
	@Bean
	public ServiceInstanceListSupplier discoveryClientServiceInstanceListSupplier(
			ConfigurableApplicationContext context) {
		return ServiceInstanceListSupplier.builder()
				.withDiscoveryClient()
				.withHints()
				.build(context);
	}
}
