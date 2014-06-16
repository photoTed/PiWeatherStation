package com.d2g.mead.conditions;

public class Conditions {

	private static final String BASE_URL = "http://api.wunderground.com/api/%%%ID%%%/conditions/q/%%%STATE%%%/%%%CITY%%%.json";

	private Image image;
	private DisplayLocation display_location;
	private String weather;
	private String temp_f;
	private String temp_c;
	private String wind_string;
	private String windchill_f;
	private String icon;
	private String icon_url;
	
	public Conditions() {}

	public void report() {
		System.out.println(display_location.getFull() + " " + temp_f + "\u00b0F (" + temp_c + "\u00b0C) chill:" + windchill_f + "\u00b0F  Wind: " + wind_string + "\n  " + image.getUrl());
	}

	public Image getImage() {
		return image;
	}

	public DisplayLocation getDisplay_location() {
		return display_location;
	}

	public static String getBaseUrl() {
		return BASE_URL;
	}

	public String getWeather() {
		return weather;
	}

	public String getTemp_f() {
		return temp_f;
	}

	public String getTemp_f_string() {
		try {
			Float temp = Float.valueOf(temp_f);
			if ( temp<-50 ) {
				return "";
			}
			return temp_f + "\u00b0F";
		} catch (NumberFormatException e) {
			return "";
		}
	}

	public String getTemp_c() {
		return temp_c;
	}

	public String getTemp_c_string() {
		try {
			Float temp = Float.valueOf(temp_c);
			if ( temp<-50 ) {
				return "";
			}
			return temp_c + "\u00b0C";
		} catch (NumberFormatException e) {
			return "";
		}
	}

	public String getWind_string() {
		return wind_string;
	}

	public String getWindchill_f() {
		return windchill_f;
	}


	public String getWindchill_f_string() {
		try {
			Float temp = Float.valueOf(windchill_f);
			if ( temp<-50 ) {
				return "";
			}
			return windchill_f + "\u00b0C";
		} catch (NumberFormatException e) {
			return "";
		}
	}

	public String getIcon() {
		return icon;
	}

	public String getIcon_url() {
		return icon_url;
	};

	public String getTempString() {
		return temp_f + "\u00b0F (" + temp_c + "\u00b0C)";
	}

	public String getChillString() {
		return windchill_f + "\u00b0F";
	}

}
