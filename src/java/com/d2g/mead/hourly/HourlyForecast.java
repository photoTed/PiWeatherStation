package com.d2g.mead.hourly;

public class HourlyForecast {

	private FctTime FCTTIME;
	private String condition;
	private String icon;
	private String icon_url;
	private HourlyTemp temp;
	private String wx;
	private HourlyTemp windchill;
	
	public HourlyForecast() {}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(FCTTIME.getWeekday_name_abbrev()).append(" ");
		sb.append(FCTTIME.getCivil()).append(" ");
		sb.append(condition).append(" ");
		sb.append(temp.toString());
		sb.append(" ").append(wx);
		sb.append(". [chill ").append(windchill.toString()).append("]");
		return sb.toString();
	}

	public FctTime getFCTTIME() {
		return FCTTIME;
	}

	public String getCondition() {
		return condition;
	}

	public String getIcon() {
		return icon;
	}

	public String getIcon_url() {
		return icon_url;
	}

	public HourlyTemp getTemp() {
		return temp;
	}

	public String getWx() {
		return wx;
	}

	public String getWxPlus() {
		if ( wx.length()==0 ) {
			return condition;
		}
		return wx;
	}

	public HourlyTemp getWindchill() {
		return windchill;
	}

}
