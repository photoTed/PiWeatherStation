package com.d2g.mead.forecast;

public class ForecastDay {

	private ForecastDate date;
	private int period;
	private ForecastTemp high;
	private ForecastTemp low;
	private String conditions;
	private String icon;
	private String icon_url;
	private TxtForecastDay dayForecast;
	private TxtForecastDay nightForecast;
	
	public ForecastDay() {}

	public ForecastDate getDate() {
		return date;
	}

	public int getPeriod() {
		return this.period;
	}

	public String getHigh() {
		return high.getFahrenheit();
	}

	public String getLow() {
		return low.getFahrenheit();
	}

	public String getConditions() {
		return conditions;
	}

	public String getIcon() {
		return icon;
	}

	public String getIcon_url() {
		return icon_url;
	}

	public TxtForecastDay getDayForecast() {
		return dayForecast;
	}

	public void setDayForecast(TxtForecastDay dayForecast) {
		this.dayForecast = dayForecast;
	}

	public TxtForecastDay getNightForecast() {
		return nightForecast;
	}

	public void setNightForecast(TxtForecastDay nightForecast) {
		this.nightForecast = nightForecast;
	}

}
