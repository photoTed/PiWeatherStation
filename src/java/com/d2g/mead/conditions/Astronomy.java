package com.d2g.mead.conditions;

import com.d2g.mead.weather.GsonTarget;

public class Astronomy extends GsonTarget {

	private Moon moon_phase;

	public Astronomy() {}

	public Moon getMoon_phase() {
		return moon_phase;
	}

	public Sun getSunrise() {
		return moon_phase.getSunrise();
	}

	public Sun getSunset() {
		return moon_phase.getSunset();
	}

	public String getDaylight() {
		return moon_phase.getDaylight();
	}
}
