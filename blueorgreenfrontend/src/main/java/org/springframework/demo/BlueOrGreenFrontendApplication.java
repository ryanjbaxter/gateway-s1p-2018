package org.springframework.demo;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class BlueOrGreenFrontendApplication {
	private static final Logger log = LoggerFactory.getLogger(BlueOrGreenFrontendApplication.class);

	@Autowired
	RestTemplate rest;

	@Value("${removeTypeCookie:true}")
	private boolean removeTypeCookie;

	public static void main(String[] args) {
		SpringApplication.run(BlueOrGreenFrontendApplication.class, args);
	}

	private final static Pattern pattern = Pattern.compile(" *; *");

	@RequestMapping("/color")
	public String color(HttpServletRequest request, HttpServletResponse response) throws URISyntaxException {
		String cookies = request.getHeader("cookie");
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		if (removeTypeCookie) {
			cookies = removeCookie(cookies, "type");
		}
		if (cookies != null && cookies.length() > 0) {
				headers.set("cookie", cookies);
		}

		if(request.getCookies() != null && !Arrays.stream(request.getCookies()).anyMatch(cookie -> cookie.getName().equals("type") &&
				cookie.getValue().equals("premium"))) {
			headers.set("X-SC-LB-Hint", "nonpremium");
		}

		RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, new URI("http://blueorgreengateway/blueorgreen"));
		ResponseEntity<String> responseEntity = rest.exchange(requestEntity, String.class);
		if(responseEntity.getStatusCode().value() == HttpStatus.TOO_MANY_REQUESTS.value()) {
			log.warn("Too many requests");
			response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
			return "";
		} else {
			return responseEntity.getBody();
		}
	}

	@Component
	protected static class RateLimitErrorHandler extends DefaultResponseErrorHandler {

		@Override
		public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
			if(clientHttpResponse.getStatusCode().value() == HttpStatus.TOO_MANY_REQUESTS.value()) {
				return false;
			} else {
				return super.hasError(clientHttpResponse);
			}
		}
	}

	@Configuration
	protected static class RestTemplateConfig {

		@LoadBalanced
		@Bean
		public RestTemplate rest(RateLimitErrorHandler rateLimitErrorHandler) {
			return new RestTemplateBuilder().errorHandler(rateLimitErrorHandler).build();
		}
	}

	private String removeCookie(String cookies, String cookieName) {
		String newCookies = "";
		if(cookies != null) {
			log.info("Got cookies: {}", cookies);
			String[] tokens = pattern.split(cookies);
			for(String token : tokens) {
				if (!token.substring(0, token.indexOf("=")).equals(cookieName)) {
					if (newCookies.length() > 0) {
						newCookies = newCookies.concat(" ; ");
					}
					newCookies = newCookies.concat(token);
				}
			}
			log.info("Processed cookies: {}", newCookies);
		}
		return newCookies;
	}

}
