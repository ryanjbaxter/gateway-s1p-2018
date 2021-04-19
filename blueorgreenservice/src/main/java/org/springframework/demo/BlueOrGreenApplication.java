package org.springframework.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties(ColorProperties.class)
public class BlueOrGreenApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlueOrGreenApplication.class, args);
	}

}
