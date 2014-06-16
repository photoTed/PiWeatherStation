package com.d2g.mead.weather;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ScheduleManager {

	private static Logger logger = Logger.getLogger(ScheduleManager.class);
	private static final List<String> weekdays = Arrays.asList(new String[]{"su","mo","tu","we","th","fr","sa"});
	private static final Map<String,String> weekdayNames;
	public static final int RANDOMIZER = (int) (3000 + 27000d * Math.random());
	public static final String OFFICE_WEEKDAY_SCHEDULE = "7-6p";
	public static final String OFFICE_WEEKEND_SCHEDULE = "";
	public static final String HOME_WEEKDAY_SCHEDULE = "6-9,6p-11p";
	public static final String HOME_WEEKEND_SCHEDULE = "7-11p";
	private Map<String,DailySchedule>dailySchedules = new HashMap<String,DailySchedule>();

	//this is executed once when the class is first loaded, before any objects are instantiated.
	static {
		Map<String,String> tmp = new HashMap<String,String>();
		tmp.put("su","Sunday");
		tmp.put("mo","Monday");
		tmp.put("tu","Tuesday");
		tmp.put("we","Wednesday");
		tmp.put("th","Thursday");
		tmp.put("fr","Friday");
		tmp.put("sa","Saturday");
		weekdayNames = Collections.unmodifiableMap(tmp);
	}

	public ScheduleManager() {}


	/**
	 * This takes the default and user properties and creates the daily schedules
	 * for every day of the week.
	 * 
	 * @param defaultProperties
	 * @param properties
	 * @throws NumberFormatException
	 */
	public void load(Properties defaultProperties, Properties properties) throws NumberFormatException {
		dailySchedules = new HashMap<String,DailySchedule>();
		//figure out if there are any user properties or not;
		boolean loadUserProperties = false;
		if (properties.getProperty("schedule")!=null ) {
			loadUserProperties = true;
		} else {
			for (String weekday:weekdays ) {
				if ( properties.getProperty("schedule." + weekday,null)!=null ) {
					loadUserProperties = true;
					break;
				}
			}
		}

		Properties scheduleProperties;
		if ( loadUserProperties ) {
			scheduleProperties = properties;
		} else {
			scheduleProperties = defaultProperties;
		}
		if ( scheduleProperties.getProperty("schedule").equalsIgnoreCase("office") || scheduleProperties.getProperty("schedule").equalsIgnoreCase("home") ) {
			setPresetSchedules( scheduleProperties.getProperty("schedule"));
		} else {
			setSchedules(scheduleProperties);
		}
	}

	public void clear() {
		for ( Map.Entry<String, DailySchedule>entry:dailySchedules.entrySet() ) {
			entry.getValue().reset();
		}
	}

	public void setSchedules(Properties scheduleProperties) {
		String defaultScheduleProperty = scheduleProperties.getProperty("schedule", null);
		if ( defaultScheduleProperty==null ) {
			defaultScheduleProperty = "0-24";
		}
		DailySchedule lastDailySchedule = null;
		logger.info("Refresh Schedules");
		for (String weekday:weekdays ) {
			String dailyScheduleProperty = scheduleProperties.getProperty("schedule." + weekday, null);
			DailySchedule dailySchedule;
			if ( dailyScheduleProperty!=null ) {
				dailySchedule = new DailySchedule(dailyScheduleProperty);
			} else {
				dailySchedule = new DailySchedule(defaultScheduleProperty);
			}
			if ( lastDailySchedule!=null ) {
				lastDailySchedule.setNextDailySchedule(dailySchedule);
			}
			dailySchedules.put(weekday, dailySchedule);
			lastDailySchedule = dailySchedule;
			logger.info("   " + weekday + " " + dailySchedule.getScheduleString());
		}
		dailySchedules.get("sa").setNextDailySchedule(dailySchedules.get("su"));
	}

	public void setPresetSchedules(String presetSchedule) {
		String weekdaySchedule;
		String weekendSchedule;
		if ( presetSchedule.equalsIgnoreCase("office") ) {
			weekdaySchedule = OFFICE_WEEKDAY_SCHEDULE;
			weekendSchedule = OFFICE_WEEKEND_SCHEDULE;
		} else {
			weekdaySchedule = HOME_WEEKDAY_SCHEDULE;
			weekendSchedule = HOME_WEEKEND_SCHEDULE;
		}
		DailySchedule lastDailySchedule = null;
		for (String weekday:weekdays ) {
			DailySchedule dailySchedule;
			if ( weekday.equals("su") || weekday.equals("sa") ) {
				dailySchedule = new DailySchedule(weekendSchedule);
			} else {
				dailySchedule = new DailySchedule(weekdaySchedule);
			}
			if ( lastDailySchedule!=null ) {
				lastDailySchedule.setNextDailySchedule(dailySchedule);
			}
			dailySchedules.put(weekday, dailySchedule);
			lastDailySchedule = dailySchedule;
			logger.info("   " + weekday + " " + dailySchedule.getScheduleString());
		}
		dailySchedules.get("sa").setNextDailySchedule(dailySchedules.get("su"));
	}

	/**
	 * Based on the number of minutes from now (minuteInterval), find the daily
	 * schedule for the event.
	 * 
	 * @param minuteInterval
	 * @return
	 */

	public DailySchedule getDailySchedule(int minuteInterval) {
		Date now = new Date(new Date().getTime() + 60l * 1000 * minuteInterval);
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
		case (Calendar.SUNDAY):
			return dailySchedules.get("su");
		case (Calendar.MONDAY):
			return dailySchedules.get("mo");
		case (Calendar.TUESDAY):
			return dailySchedules.get("tu");
		case (Calendar.WEDNESDAY):
			return dailySchedules.get("we");
		case (Calendar.THURSDAY):
			return dailySchedules.get("th");
		case (Calendar.FRIDAY):
			return dailySchedules.get("fr");
		default:
			return dailySchedules.get("sa");
		}
	}


	/**
	 * 
	 * @return HTML formated schedule used on the web service.
	 */

	public String getHtmlSchedule(){
		StringBuilder sb = new StringBuilder();
		sb.append("  <table class=\"indent \" >\n");
		for ( String weekday:weekdays ) {
			sb.append("    <tr>\n");
			sb.append("      <td class=\"tableCell\" >").append(weekdayNames.get(weekday)).append("</td>\n");
			sb.append("      <td class=\"tableCell\" >").append(dailySchedules.get(weekday).getScheduleString()).append("</td\n>");
			sb.append("    </tr>\n");
		}
		sb.append("  </table>\n");
		return sb.toString();
	}
}
