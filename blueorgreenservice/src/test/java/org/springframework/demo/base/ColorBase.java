package org.springframework.demo.base;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

import org.junit.Before;
import org.springframework.demo.BlueOrGreenApplication;
import org.springframework.demo.ColorController;
import org.springframework.demo.ColorProperties;

/**
 * @author Ryan Baxter
 */
public class ColorBase {

	@Before
	public void setup() {
		ColorProperties colorProperties = new ColorProperties();
		colorProperties.setColor("blue");
		RestAssuredMockMvc.standaloneSetup(new ColorController(colorProperties));
	}
}
