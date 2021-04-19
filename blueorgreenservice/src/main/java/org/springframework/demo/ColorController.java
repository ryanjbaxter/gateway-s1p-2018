package org.springframework.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ryan Baxter
 */
@RestController
public class ColorController {

	private ColorProperties colorProperties;

	public ColorController(ColorProperties colorProperties) {
		this.colorProperties = colorProperties;
	}

	@GetMapping(path = {"/blueorgreen", "/"})
	public Color color() throws InterruptedException {
		if (colorProperties.isSlow()) {
			Thread.sleep(5000);
		}
		if (Color.BLUE.getId().equalsIgnoreCase(colorProperties.getColor())) {
			return Color.BLUE;
		} else if (Color.YELLOW.getId().equalsIgnoreCase(colorProperties.getColor())) {
			return Color.YELLOW;
		}

		return Color.GREEN;
	}

	static class Color {
		public static final Color GREEN = new Color("green");
		public static final Color BLUE = new Color("blue");
		public static final Color YELLOW = new Color("yellow");

		private String id;

		Color(){}

		public Color(String id) { this.id = id; }

		public String getId() {
			return id;
		}
	}
}
