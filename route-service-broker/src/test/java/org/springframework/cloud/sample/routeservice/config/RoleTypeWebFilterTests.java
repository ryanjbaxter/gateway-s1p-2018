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

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.server.context.SecurityContextServerWebExchangeWebFilter;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;

/**
 * @author Rob Winch
 */
public class RoleTypeWebFilterTests {

    private MockController controller = new MockController();

    private RoleTypeWebFilter filter = new RoleTypeWebFilter();

    private WebTestClient client = WebTestClient.bindToController(this.controller)
            .apply(springSecurity())
            .webFilter(new SecurityContextServerWebExchangeWebFilter(), this.filter)
            .build();

    @Test
    public void filterWhenMaliciousThenError() {
        this.client
                .get()
                .uri("/")
                .cookie("type", "premium")
                .exchange()
                .expectStatus().is5xxServerError();

        assertThat(this.controller.exchange).isNull();
    }

    @Test
    public void filterWhenNotAuthenticatedThenCookieValueIsNull() {
        this.client
                .mutateWith(mockUser("u"))
                .get()
                .uri("/")
                .exchange()
                .expectStatus().isOk();

        assertThat(getCookieValue()).isNull();
    }

    @Test
    public void filterWhenPremiumThenCookieValueIsPremium() {
        this.client
                .mutateWith(mockUser("u").roles("PREMIUM"))
                .get()
                .uri("/")
                .exchange()
                .expectStatus().isOk();

        assertThat(getCookieValue()).isEqualTo("type=premium");
    }

    @Test
    public void filterWhenPremiumAndExistingThenCookieValueAppendsPremium() {
        this.client
                .mutateWith(mockUser("u").roles("PREMIUM"))
                .get()
                .uri("/")
                .header(HttpHeaders.COOKIE, "a=b")
                .exchange()
                .expectStatus().isOk();

        assertThat(getCookieValue()).isEqualTo("a=b ; type=premium");
    }

    @Test
    public void filterWhenNotPremiumThenCookieValueIsNull() {
        this.client
                .mutateWith(mockUser("u").roles("NOT"))
                .get()
                .uri("/")
                .exchange()
                .expectStatus().isOk();

        assertThat(getCookieValue()).isNull();
    }

    private String getCookieValue() {
        return this.controller.exchange.getRequest().getHeaders().getFirst(HttpHeaders.COOKIE);
    }

    @RestController
    static class MockController {
        ServerWebExchange exchange;

        @GetMapping("/")
        String index(ServerWebExchange exchange) {
            this.exchange = exchange;
            return "mock";
        }
    }

}