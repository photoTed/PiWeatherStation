package com.d2g.mead.hourly;

import java.util.ArrayList;

public class Hourly {

	private static final String BASE_URL = "http://api.wunderground.com/api/%%%ID%%%/hourly/q/%%%STATE%%%/%%%CITY%%%.json";

	private ArrayList<HourlyForecast>hourly_forecast;
	
	public Hourly() {}

	public ArrayList<HourlyForecast> getHourly_forecast() {
		return hourly_forecast;
	};

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for ( HourlyForecast hourlyForecast:hourly_forecast ) {
			sb.append(hourlyForecast.toString()).append("\n");
		}
		return sb.toString();
	}
}
