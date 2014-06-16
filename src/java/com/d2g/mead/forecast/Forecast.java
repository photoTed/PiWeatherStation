package com.d2g.mead.forecast;

import java.util.HashMap;
import java.util.Map;

public class Forecast {

	private static final String BASE_URL = "http://api.wunderground.com/api/%%%ID%%%/forecast10day/q/%%%STATE%%%/%%%CITY%%%.json";
	private SimpleForecast simpleforecast;
	private TxtForecast txt_forecast;
	
	public Forecast() {}

	public void load() {
		Map<Integer,ForecastDay> forecastMap = new HashMap<Integer,ForecastDay>();
		for ( ForecastDay forecastDay:getSimpleForecast().getForecastday()) {
			forecastMap.put(forecastDay.getPeriod(),forecastDay);
		}
		for ( TxtForecastDay txtForecastDay:getTxtForecast().getForecastday()) {
			ForecastDay forecastDay = forecastMap.get((txtForecastDay.getPeriod()/2 + 1));
			if ( txtForecastDay.getTitle().contains("Night") ) {
				forecastDay.setNightForecast(txtForecastDay);
			} else {
				forecastDay.setDayForecast(txtForecastDay);
			}
		}
	}
	
	public void report() {
		for ( ForecastDay forecastDay:getSimpleForecast().getForecastday()) {
			System.out.print(forecastDay.getDate().getDay() + " " + forecastDay.getDate().getMonthName().substring(0,3));
			System.out.println(" Temp " + forecastDay.getHigh() + "\u00b0F/" + forecastDay.getLow() + "\u00b0F : " + forecastDay.getConditions());
			System.out.println("  Day   :" + forecastDay.getDayForecast().getFcttext());
			System.out.println("  Night :" + forecastDay.getNightForecast().getFcttext());
		}
	}

	public SimpleForecast getSimpleForecast() {
		return simpleforecast;
	}

	public TxtForecast getTxtForecast() {
		return txt_forecast;
	}

}
