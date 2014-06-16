package com.d2g.mead.conditions;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Moon {

	private String percentIlluminated;
	private String ageOfMoon;
	private Sun sunrise;
	private Sun sunset;

	public Moon() {}


	public String getPercentIlluminated() {
		return percentIlluminated;
	}

	public String getAgeOfMoon() {
		return ageOfMoon;
	}

	public Sun getSunrise() {
		return sunrise;
	}


	public Sun getSunset() {
		return sunset;
	}

	public String getDaylight() {
		int daylightMinutes = sunset.getInMinutes() - sunrise.getInMinutes();
		int hours = daylightMinutes/60;
		NumberFormat nf = new DecimalFormat("00");
		return String.valueOf(hours) + ":" + nf.format(daylightMinutes - hours*60);
	}

}
