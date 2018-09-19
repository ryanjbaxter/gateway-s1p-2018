package org.springframework.cloud.samples.authgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;

@SpringBootApplication
public class AuthgatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthgatewayApplication.class, args);
	}

	@Bean
	public RouteLocator routeLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p.path("/**").uri("lb://blueorgreengateway"))
				.build();
	}

	@EnableWebFluxSecurity
	@Configuration
	public class MyExplicitSecurityConfiguration {
		@Bean
		public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
			http.authorizeExchange().pathMatchers("/", "/color").authenticated()
					.and().logout().logoutSuccessHandler(new RoleBasedServerLogoutSuccessHandler())
					.and().formLogin().authenticationSuccessHandler(new RoleBasedAuthenticationSuccessHandler())
			.and().authorizeExchange().pathMatchers("/js/**").permitAll();
			return http.build();
		}

		@Bean
		public MapReactiveUserDetailsService userDetailsService() {
			PasswordEncoder pwEncoder = new BCryptPasswordEncoder();
			UserDetails premium = User.withDefaultPasswordEncoder().username("premium").password("pw").roles("PREMIUM").build();
			UserDetails basic = User.withDefaultPasswordEncoder().username("user").password("pw").roles("BASIC").build();
			return new MapReactiveUserDetailsService(premium, basic);
		}
	}
}
