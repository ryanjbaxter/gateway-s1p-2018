package org.springframework.demo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
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
import org.springframework.http.RequestEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class BlueOrGreenFrontendApplication {
	private final Logger log = LoggerFactory.getLogger(BlueOrGreenFrontendApplication.class);

	@Autowired
	RestTemplate rest;

	@Value("${removeTypeCookie:true}")
	private boolean removeTypeCookie;

	public static void main(String[] args) {
		SpringApplication.run(BlueOrGreenFrontendApplication.class, args);
	}

	private final static Pattern pattern = Pattern.compile(" *; *");

	@RequestMapping("/color")
	public String color(HttpServletRequest request) throws URISyntaxException {
		String cookies = request.getHeader("cookie");
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		if (removeTypeCookie) {
			cookies = removeCookie(cookies, "type");
		}
		if (cookies != null && cookies.length() > 0) {
				headers.set("cookie", cookies);
		}

		RequestEntity requestEntity = new RequestEntity(headers, HttpMethod.GET, new URI("http://blueorgreengateway/blueorgreen"));
		return rest.exchange(requestEntity, String.class).getBody();
	}

	@Configuration
	protected static class RestTemplateConfig {

		@LoadBalanced
		@Bean
		public RestTemplate rest() {
			return new RestTemplateBuilder().build();
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
