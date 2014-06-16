package com.d2g.mead.weather;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class DailySchedule {

	private List<Integer> onHours = new LinkedList<Integer>();
	private DailySchedule nextDailySchedule;
	private String scheduleString;


	/**
	 * This controls when during a given day, an event may be executed.  As this
	 * application uses up weather.com resources at no cost, it seemed polite and
	 * responsible to minimize the amount of times the application accesses that
	 * data.  If you work 9-5 M-F, set the default schedule to "6-9,6p-10p" so
	 * you get the morning weather and the evening weather but do not access 
	 * while you are at work.  Then set Saturday and Sunday to 8-10p.  Do the
	 * opposite scheduling if you run this at work.
	 * 
	 * The schedule string comes from the property file:
	 * 
	 *   schedule=6-9,6p-10p
	 *   
	 * sets the default.  Any day can be overridden with the property with the 
	 * lowercase of the first two letters of the day of week:
	 * 
	 *   schedule.sa=8-10p
	 *   schedule.su=10-10p
	 * 
	 * sets Saturday and Sunday schedules.
	 * 
	 * The values are hours separated by commas, dashes represents a range. A
	 * number with a "p" is assumed to be afternoon. Alternatively 13-24 may
	 * be used.
	 * 
	 * Two special values "all" and "none" are self-explanatory.
	 * 
	 * The constructor parses the schedule string and creates a list of allowed
	 * hours that is used in the getNextDate() method to find the next allowed
	 * date for the event to be executed.
	 * 
	 * @param scheduleString
	 * @throws NumberFormatException
	 */
	
	public DailySchedule(String scheduleString) throws NumberFormatException {
		this.scheduleString = scheduleString;
		if ( scheduleString.isEmpty() || scheduleString.contains("none") ) {
			return;
		}
		if ( scheduleString.toLowerCase().equals("all") ) {
			for ( int i=0;i<24;i++ ) {
				onHours.add(i);
			}
			return;
		}
		String[] scheduleParts = scheduleString.toLowerCase().replace(" ","").split("[,;]");
		int lastHour = -1;
		for (int i=0;i<scheduleParts.length;i++ ) {
			if ( scheduleParts[i].contains("-") ) {
				String[]rangeParts = scheduleParts[i].split("-");
				if ( rangeParts.length!=2 ) {
					throw new NumberFormatException("Invalid range in schedule string: " + scheduleParts[i]);
				}
				int startHour = convertToHour(rangeParts[0],lastHour);
				int endHour = convertToHour(rangeParts[1],startHour);
				for (int hour=startHour;hour<endHour;hour++) {
					onHours.add(hour);
				}
			} else {
				int hour = convertToHour(scheduleParts[i],lastHour);
				onHours.add(hour);
				lastHour = hour;
			}
		}
		if ( onHours.size()==0 ) {
			throw new NumberFormatException("No valid schedule was found in " + scheduleString);
		}
	}

	private int convertToHour(String hourString, int lastHour) throws NumberFormatException {
		int hour = Integer.valueOf(hourString.replaceAll("\\D", ""));
		if ( hour==12 ) {
			if ( hourString.contains("a") ) {
				hour = 24;
			}
		} else if ( hourString.contains("p") ) {
			hour = hour + 12;
		}
		if ( hour<lastHour || hour>24 ) {
			throw new NumberFormatException("Invalid hour in schedule string: " + hourString);
		}
		return hour;
	}


	/**
	 * If the event cannot be executed on this day, the schedule needs to have the
	 * schedule for the next day to see when within that day the event can be 
	 * executed.  Every schedule has the following day's schedule, and the last
	 * day points to the first making a ring.
	 * 
	 * @param nextDailySchedule
	 */
	public void setNextDailySchedule(DailySchedule nextDailySchedule) {

		this.nextDailySchedule = nextDailySchedule;
	}

	public Date getNextDate(int minuteInterval) {
		return getNextDate(minuteInterval,true);
	}

	
	/**
	 * This method takes the interval in minutes when the event should be executed next.  If
	 * 10 is passed in, the event should be executed in 10 minutes from now.  But, there are 
	 * two other variables: the schedule for the day and can this event be executed some time
	 * the next day (nextDay=true) or must it be executed no later than midnight tonight (really
	 * 00:00:00 tomorrow).
	 * 
	 * The schedule restricts by hour, that is a schedule of 9-6p allows the event to be 
	 * execute any time between 9:00 a.m. and 6:00 p.m.  That is controlled by a list of
	 * hours "onHours" that has a element for each allowable hour, so in the above onHours
	 * contains "6,7,8,9,10,11,12,13,14,15,16,17).  NOTE: the end of the range is not included
	 * in the list.
	 * 
	 * @param minuteInterval
	 * @param nextDay
	 * @return the next time an event should be executed.
	 */

	public Date getNextDate(int minuteInterval, boolean nextDay) {
		Calendar cal = Calendar.getInstance();
		Calendar now = Calendar.getInstance();
		// based on the interval, what time should this event be executed next.
		// the rest of the method checks the schedule to see if it is allowed to
		// execute at that time and if not, when is the next allowed time.
		cal.setTime(new Date(new Date().getTime() + 60l * 1000 * minuteInterval));
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, ScheduleManager.RANDOMIZER);
		now.setTime(new Date());
		// if the interval puts it into the next day and it can't be, set the time for
		// midnight tonight.
		if ( cal.get(Calendar.DAY_OF_YEAR) != now.get(Calendar.DAY_OF_YEAR) && !nextDay ) {
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			return cal.getTime();
		}
		if ( onHours.contains(cal.get(Calendar.HOUR_OF_DAY)) ) {
			return cal.getTime();
		}
		// if it's exactly the hour i.e. 10:00:00, test for a second before, i.e. 09:59:59.  That accommodates
		// the exact end of a range, i.e. 6-10 (10 is not included on the list, but 9 is)
		cal.add(Calendar.MILLISECOND, -(ScheduleManager.RANDOMIZER+1));
		if ( onHours.contains(cal.get(Calendar.HOUR_OF_DAY)) ) {
			cal.add(Calendar.MILLISECOND, (ScheduleManager.RANDOMIZER+1));
			return cal.getTime();
		}
		cal.add(Calendar.MILLISECOND, (ScheduleManager.RANDOMIZER+1));
		cal.set(Calendar.MINUTE, 0);
		// if it's before the first scheduled hour set it to the first scheduled hour.
		if ( onHours.size()>0 && cal.get(Calendar.HOUR_OF_DAY)<onHours.get(0) ) {
			cal.set(Calendar.HOUR_OF_DAY, onHours.get(0));
			return cal.getTime();
		}
		// iterate over all the hours seeing if this hour is between two hours. If so set to the later hour, for example
		// if the schedule is 6-9, 5p-10p the onHours contains 6,7,8,17,18,19,20,21.  If the test hour is 11, between
		// 8 and 17, set the hour to 17, the next scheduled time.
		for ( int i=0;i<onHours.size()-1;i++ ) {
			if (cal.get(Calendar.HOUR_OF_DAY)>onHours.get(i) && cal.get(Calendar.HOUR_OF_DAY)<onHours.get(i+1)) {
				cal.set(Calendar.HOUR_OF_DAY, onHours.get(i+1));
				return cal.getTime();
			}
		}
		// if the test hour was not found, that is it is after the last scheduled hour of this day,
		// start looking at tomorrow.
		cal.add(Calendar.HOUR,24);
		// another check that it cannot be tomorrow, that's probably redundant...
		if ( !nextDay ) {
			cal.set(Calendar.HOUR_OF_DAY, 0);
			return cal.getTime();
		}
		//get the schedule for the day after this one, looking 
		//for a day with at least one scheduled hour. This skips
		//over days that have no schedule, say the weekend and this
		//is located in your office or at school.
		DailySchedule finalDailySchedule = nextDailySchedule;
		while ( finalDailySchedule.getOnHours().size()==0 ) {
			finalDailySchedule = finalDailySchedule.getNextDailySchedule();
			cal.add(Calendar.HOUR,24);
		}
		cal.set(Calendar.HOUR_OF_DAY, finalDailySchedule.getOnHours().get(0));
		return cal.getTime();
	}
	
	public void reset() {
		onHours = new LinkedList<Integer>();
		onHours.add(0);
	}

	public List<Integer>getOnHours() {
		return onHours;
	}

	public String getScheduleString() {
		return this.scheduleString;
	}

	public DailySchedule getNextDailySchedule() {
		return this.nextDailySchedule;
	}

	
	/**
	 * testing, testing, testing :-)
	 * @param args
	 */

	public static void main(String[] args ) {
		DailySchedule app = new DailySchedule("6-10,2p,4p-10p");
		app.setNextDailySchedule(app);
		System.out.println("Next: " + app.getNextDate(3, false));

		app = new DailySchedule("6-2p,4p,6p-24");
		app = new DailySchedule("none");
		app.setNextDailySchedule(app);
		DailySchedule app2 = new DailySchedule("none");
		app.setNextDailySchedule(app2);
		DailySchedule app3 = new DailySchedule("none");
		app2.setNextDailySchedule(app3);
		DailySchedule app4 = new DailySchedule("6");
		app3.setNextDailySchedule(app4);
		System.out.println("Next: " + app.getNextDate(125));

//		app = new DailySchedule("6-10");
//		app.setNextDailySchedule(app);
//		System.out.println("Next: " + app.getNextDate(10));
//
//		app = new DailySchedule("all");
//		app.setNextDailySchedule(app);
//		System.out.println("Next: " + app.getNextDate(10,false));
//
//		app = new DailySchedule("none");
//		app2 = new DailySchedule("6");
//		app.setNextDailySchedule(app2);
//		System.out.println("Next: " + app.getNextDate(10,false));
	}
}

