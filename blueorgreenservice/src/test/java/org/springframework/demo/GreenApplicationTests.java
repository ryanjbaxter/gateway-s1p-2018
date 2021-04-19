package org.springframework.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Ryan Baxter
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"color:green", "eureka.client.enabled: false"})
public class GreenApplicationTests {
	@Autowired
	private TestRestTemplate rest;

	@Test
	public void contextLoads() {
		ColorController.Color color = rest.getForObject("/", ColorController.Color.class);
		assertEquals("green", color.getId());
	}
}
