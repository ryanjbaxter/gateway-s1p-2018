package org.springframework.demo;

import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.Cookie;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
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

	@Autowired
	RestTemplate rest;

	public static void main(String[] args) {
		SpringApplication.run(BlueOrGreenFrontendApplication.class, args);
	}

	@RequestMapping("/color")
	public String color(HttpServletRequest request) throws URISyntaxException {
		String cookies = request.getHeader("cookie");
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		if(cookies != null) {
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
}
