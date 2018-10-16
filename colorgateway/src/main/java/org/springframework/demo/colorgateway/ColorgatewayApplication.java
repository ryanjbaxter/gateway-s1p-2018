package org.springframework.demo.colorgateway;

import java.util.HashMap;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ColorgatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ColorgatewayApplication.class, args);
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p.path("/color").uri("lb://colorservice"))
                .route(p -> p.path("/").or().path("/color").or().path("/js/**").uri("lb://colorfrontend"))
                .build();
    }

    @Bean
    public LoadBalancerClientFilter loadBalancerClientFilter(LoadBalancerClient client) {
        return new org.springframework.demo.colorgateway.CustomLoadBalancerClientFilter(client);
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
