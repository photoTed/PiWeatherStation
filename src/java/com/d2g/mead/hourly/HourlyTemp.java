package com.d2g.mead.hourly;

public class HourlyTemp {

	private String english;
	private String metric;
	
	public HourlyTemp() {}

	public String toString() {
		return english + "\u00b0F (" + metric + "\u00b0C)";
	}

	public String getEnglishString() {
		try {
			Integer temp = Integer.valueOf(english);
			if ( temp<-50 ) {
				return "";
			}
			return english + "\u00b0F";
		} catch (NumberFormatException e) {
			return "";
		}
	}

	public String getMetricString() {
		try {
			Integer temp = Integer.valueOf(metric);
			if ( temp<-50 ) {
				return "";
			}
			return metric + "\u00b0C";
		} catch (NumberFormatException e) {
			return "";
		}
	}
}
