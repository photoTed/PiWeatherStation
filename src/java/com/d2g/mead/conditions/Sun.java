package com.d2g.mead.conditions;

public class Sun {

	private String hour;
	private String minute;

	public Sun() {}
	
	public String getTime() {
		try {
			int hourInt = Integer.parseInt(hour);
			Integer.parseInt(minute);
			if ( hourInt>12 ) {
				return String.valueOf(hourInt-12) + ":" + minute + "pm";
			} else {
				return hour + ":" + minute + "am";
			}
				
		} catch (NumberFormatException e) {
			return "n/a";
		}
	}

	public int getInMinutes() {
		return Integer.parseInt(hour) * 60 + Integer.parseInt(minute);
	}
}

