package org.springframework.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Ryan Baxter
 */
@ConfigurationProperties
public class ColorProperties {

	private String color;

	private boolean slow = false;

	public boolean isSlow() {
		return slow;
	}

	public void setSlow(boolean slow) {
		this.slow = slow;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
