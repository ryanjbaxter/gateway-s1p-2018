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

import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        // @formatter:off
        http
            .addFilterAt(new HeaderFilter(), SecurityWebFiltersOrder.FIRST)
            .addFilterAt(new UndoHeaderFilter(), SecurityWebFiltersOrder.LAST)
            .addFilterAt(new RoleTypeWebFilter(), SecurityWebFiltersOrder.LAST)
            .csrf().disable()
            .authorizeExchange()
                .pathMatchers("/v2/**").hasRole("ADMIN")
                .matchers(EndpointRequest.to("info", "health")).permitAll()
                .matchers(EndpointRequest.toAnyEndpoint()).hasRole("ADMIN")
                .pathMatchers("/images/**").permitAll()
                .anyExchange().authenticated()
                .and()
            .formLogin()
                .and()
            .httpBasic();
        // @formatter:on
        return http.build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        // @formatter:off
        User.UserBuilder builder = User.withDefaultPasswordEncoder();
        UserDetails admin = builder.username("admin")
                .password("supersecret")
                .roles("ADMIN")
                .build();
        UserDetails trial = builder.username("trial")
                .password("pw")
                .roles("TRIAL")
                .build();
        UserDetails basic = builder.username("basic")
                .password("pw")
                .roles("BASIC")
                .build();
        UserDetails premium = builder.username("premium")
                .password("pw")
                .roles("PREMIUM")
                .build();
        // @formatter:on
        return new MapReactiveUserDetailsService(admin, trial, premium, basic);
    }

}
