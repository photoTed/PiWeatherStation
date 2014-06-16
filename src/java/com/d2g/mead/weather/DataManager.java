package com.d2g.mead.weather;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.d2g.mead.conditions.AstronomyFeed;
import com.d2g.mead.conditions.ConditionsFeed;
import com.d2g.mead.conditions.RadarFeed;
import com.d2g.mead.forecast.ForecastFeed;
import com.d2g.mead.hourly.HourlyFeed;

/**
 * Only one instance of the DataManager (singleton) is created and it manages  
 * feeding the data to each of frames.  That data is loaded from the URLs 
 * for each type of data:
 *   Astronomy		The sunrise/set and phase of the moon, displayed on the conditions frame.
 *   Conditions		The daily conditions.
 *   ForeCast		The multiday forecast.
 *   Hourly			The hourly forecast.
 *   Radar			The radar map displayed on the conditions frame.
 * 
 * Each data feed is controlled by an instance of classes extended from the DataFeed class.
 * 
 * Each feed (expect for Astronomy which updates at 4:00 a.m. every day) has an update interval,
 * which is the number of minutes between refreshing the data.
 * 
 * @author Ted Mead
 *
 */
public class DataManager {

	private static Logger logger = Logger.getLogger(DataManager.class);
	
	private String state;
	private String location;
	private String weatherKey;
	private SortedMap<String,DataFeed>dataFeeds = new TreeMap<String,DataFeed>();
	private Date nextRegistration = null;
	private int port;
	private Set<String>updateListeners = new HashSet<String>();
	private String weatherMaster;

	private FrameManager frameManager;
	private ScheduleManager scheduleManager = new ScheduleManager();
	private Timer registrationTimer = null;

	public DataManager(String state, String location, String weatherKey) {
		scheduleManager.load(Weather.defaultProperties, Weather.properties);
		this.state = state;
		this.location = location;
		this.weatherKey = weatherKey;

		port = getIntegerProperty("server.port", Integer.valueOf(Weather.defaultProperties.getProperty("server.port")));
		//check if you have set the weather.master property
		String weatherMaster = Weather.properties.getProperty("weather.master");
		if ( weatherMaster!=null ) {
			registerMasterIpAddress(weatherMaster);
		}
	}

	public DataManager(String weatherMaster) {
		scheduleManager.load(Weather.defaultProperties, Weather.properties);
		this.weatherMaster = weatherMaster;
		port = getIntegerProperty("server.port", Integer.valueOf(Weather.defaultProperties.getProperty("server.port")));
		registerMasterIpAddress(weatherMaster);
	}

	public void init() throws IOException {
		//set up all the feeds.
		DataFeed dataFeed = new AstronomyFeed(this);
		dataFeeds.put(dataFeed.getName(), dataFeed);
		dataFeed = new ConditionsFeed(this);
		dataFeeds.put(dataFeed.getName(), dataFeed);
		dataFeed = new ForecastFeed(this);
		dataFeeds.put(dataFeed.getName(), dataFeed);
		dataFeed = new HourlyFeed(this);
		dataFeeds.put(dataFeed.getName(), dataFeed);
		dataFeed = new RadarFeed(this);
		dataFeeds.put(dataFeed.getName(), dataFeed);

	}

	public void setFrameManager(FrameManager frameManager) {
		this.frameManager = frameManager;
	}

	public void update(String feedName) {
		try {
			dataFeeds.get(feedName).update(true);
			missedSchedules();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void registerMasterIpAddress(String tmpWeatherMaster) {
		//for a slave machine, check every 20 minutes that the master is still alive.
		if ( tmpWeatherMaster==null ) {
			tmpWeatherMaster = Weather.properties.getProperty("weather.master");
		}
		if ( registrationTimer!=null ) {
			registrationTimer.cancel();
		}
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		now.add(Calendar.MINUTE, 20);
		now.set(Calendar.SECOND,0);
		now.set(Calendar.MILLISECOND, 0);
		nextRegistration = now.getTime();
		HttpURLConnection conn = null;
		URL url = null;
		try {
			url = new URL("http://" + tmpWeatherMaster + ":" + port + "/registerListener" );
			conn = (HttpURLConnection) url.openConnection();
			if ( conn.getResponseCode()==200 ) {
				//if the master responds and it is different that the one stored (or null first pass), update
				//the data feeds.
				logger.info("Registered to master at " + tmpWeatherMaster);
				if ( !tmpWeatherMaster.equals(weatherMaster) ) {
					weatherMaster = tmpWeatherMaster;
					scheduleManager.clear();
					for ( Map.Entry<String, DataFeed>entry:dataFeeds.entrySet() ) {
						entry.getValue().setMasterUrl(weatherMaster, port);
					}
				}
			} else {
				logger.error("Could not register to master at " + url);
				weatherMaster = null;
			}		
		} catch (MalformedURLException e) {
			logger.error("Malformed URL exception registering to " + url);
			weatherMaster = null;
		} catch (IOException e) {
			logger.error("IO exception registering to " + url);
			weatherMaster = null;
		}
		if ( weatherMaster==null ) {
			scheduleManager.load(Weather.defaultProperties, Weather.properties);
			setStandAloneUrls();
		}

		TimerTask task = new TimerTask() {
	        public void run() {
				registerMasterIpAddress(weatherMaster);
	        }
	    };
	    registrationTimer = new Timer();
	    logger.debug("Next registration scheduled for: " + nextRegistration);
	    registrationTimer.schedule(task, nextRegistration);
	}

	public void removeMasterIpAddress(String tmpMasterIpAddress) {
		HttpURLConnection conn = null;
		try {
			URL url = new URL("http://" + tmpMasterIpAddress + ":" + port + "/removeListener/" );
			conn = (HttpURLConnection) url.openConnection();
			if ( conn.getResponseCode()==200 ) {
				logger.info("Removed listener from " + tmpMasterIpAddress);
			} else {
				logger.error("Could not remove listener from " + weatherMaster);
			}		
		} catch (MalformedURLException e) {
			logger.error("Malformed URL exception removing listener from " + weatherMaster);
		} catch (IOException e) {
			logger.error("IO exception removing listener from " + weatherMaster);
		}
		if ( tmpMasterIpAddress.equals(weatherMaster) ) {
			weatherMaster = null;
			scheduleManager.load(Weather.defaultProperties, Weather.properties);
			setStandAloneUrls();
		}
	}

	public void addListener(String listenerAddress) {
		updateListeners.add(listenerAddress);
	}

	public void removeListener(String listenerAddress) {
		updateListeners.remove(listenerAddress);
	}

	//synchronized means only one thread can run this method at one time. There is no
	//benefit to sending multiple events to the listeners simultaneously.
	synchronized public void notifyListeners(String event) {
		final String finalEvent = event;
		if ( updateListeners.size()==0 ) {
			return;
		}
		//by creating a separate thread, this processing does not interfere with the other tasks.
		Thread notifyThread = new Thread() {
			public void run() {
				//if a listener does not respond and is removed from the updateListeners set but if
				//you try to remove an element from a collection while looping over it you get into
				//trouble, so you use the Iterator class that is built for this.
				Iterator<String> it = updateListeners.iterator();
				while ( it.hasNext() ) {
					String listenerAddress = it.next();
					logger.info("notifying listener at " + listenerAddress + " for event " + finalEvent);
					int loopCount = 0;
					boolean done = false;
					//when it comes to networking, it's wise to try more than once to get through.
					while ( !done && loopCount<4 ) {
						//make sure to increment the counter or you may loop forever.
						loopCount++;
						HttpURLConnection conn = null;
						String urlString = "invalid URL to " + listenerAddress;
						try {
							//if it is not the first time through, wait a couple of seconds
							if ( loopCount>1 ) {
								Thread.sleep(2000);
							}
							URL url = new URL("http://" + listenerAddress + ":" + port + "/updateEvent/" + finalEvent);
							urlString = url.toString();
							conn = (HttpURLConnection) url.openConnection();
							if ( conn.getResponseCode()==200 ) {
								logger.info("Listener event " + finalEvent + " sent to " + listenerAddress);
								done=true;
							} else {
								logger.error("Failed to send listener event " + finalEvent + " to " + listenerAddress + " [loopCont=" + loopCount + "]");
							}
						} catch (MalformedURLException e) {
							logger.error("MalformedURL exception.  Failed to send listener event " + finalEvent + " to " + urlString);
							done = true;
							it.remove();
						} catch (IOException e) {
							logger.error("IO exception.  Failed to send listener event " + finalEvent + " to " + urlString + " [loopCont=" + loopCount + "]");
						} catch (InterruptedException e) {
							logger.error(e);
						}
					}
					if ( !done ) {
						logger.error("removing listener at " + listenerAddress);
						it.remove();
					}
				}
			}

		};
		notifyThread.start();
	}

	private void setStandAloneUrls() {
		for ( Map.Entry<String, DataFeed>entry:dataFeeds.entrySet() ) {
			entry.getValue().setProperties();
		}
	}
	
	private void missedSchedules() throws Exception {
		long now = new Date().getTime() - 10000l;
		for ( Map.Entry<String, DataFeed>entry:dataFeeds.entrySet() ) {
			Date nextScheduledUpdate = entry.getValue().getNextScheduledUpdate();
			if ( nextScheduledUpdate!=null && nextScheduledUpdate.getTime()-now<0 )
				logger.error(entry.getValue().getName() + " update scheduled was scheduled for " + nextScheduledUpdate + " and was missed.");
				entry.getValue().update(true);
		}
	}
	
	public String readUrl(String urlString) throws Exception {
	    BufferedReader reader = null;
	    try {
	        URL url = new URL(urlString);
	        reader = new BufferedReader(new InputStreamReader(url.openStream()));
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1) {
	            buffer.append(chars, 0, read); 
	        }
	        logger.debug(urlString);
	        return buffer.toString();
	    } finally {
	        if (reader != null)
	            reader.close();
	    }
	}

	public int getPort() {
		return this.port;
	}

	public String getWeatherMaster() {
		return this.weatherMaster;
	}

	public String getHtmlUpdate(){
		StringBuilder sb = new StringBuilder();
		sb.append("  <table class=\"indent \" >\n");
		sb.append("    <tr>\n");
		sb.append("      <th class=\"tableCell\" width=\"175px\">Frame</td>\n");
		sb.append("      <th class=\"tableCell\" >Last Update</td>\n");
		sb.append("      <th class=\"tableCell\" >Next Update</td>\n");
		sb.append("    </tr>\n");
		
		for ( Map.Entry<String, DataFeed>entry:dataFeeds.entrySet() ) {
			sb.append(entry.getValue().getHtml());
		}

		sb.append("    <tr>\n");
		sb.append("      <td class=\"tableCell\" colspan=\"3\" style=\"text-align:center;\"><a href=\"/refresh/all\">refresh all now<a></td>\n");
		sb.append("    </tr>\n");
		sb.append("  </table>\n");
		return sb.toString();
	}

	public String getHtmlSettings() {
		StringBuilder sb = new StringBuilder();
		sb.append("  <table class=\"indent settingsCell \" >\n");
		sb.append(frameManager.getConditionsFrame().getHtmlSettings());
		sb.append(frameManager.getForecastFrame().getHtmlSettings());
		sb.append(frameManager.getHourlyFrame().getHtmlSettings());
		for ( Map.Entry<String, DataFeed>entry:dataFeeds.entrySet() ) {
			sb.append("    <tr><td>" + entry.getValue().getName() + ".url=").append(entry.getValue().getUrl()).append("</td></tr>");
		}

		for ( Map.Entry<String, DataFeed>entry:dataFeeds.entrySet() ) {
			sb.append("    <tr><td>" + entry.getValue().getName() + ".refresh.interval=").append(entry.getValue().getUpdateInterval()).append("</td></tr>");
		}

		sb.append("  </table>\n");
		sb.append("  <p>").append(WebServer.HOME_LINK).append("</p>\n");

		sb.append("<H4>Local Settings</h4>");
		sb.append("  <table class=\"indent settingsCell \" >\n");
		Map<String,String>propertiesMap = new TreeMap<String,String>();
		for (Object key:Weather.properties.keySet()) {
			propertiesMap.put((String)key, Weather.properties.getProperty((String)key));
		}
		for ( Map.Entry<String, String>entry:propertiesMap.entrySet() ) {
			sb.append("    <tr><td>").append(entry.getKey()).append("=").append(entry.getValue()).append("</td></tr>");
		}
		sb.append("  </table>\n");
		sb.append("  <p>").append(WebServer.HOME_LINK).append("</p>\n");

		sb.append("<H4>Default Settings</h4>");
		sb.append("  <table class=\"indent settingsCell \" >\n");
		propertiesMap = new TreeMap<String,String>(new PropertyComparator());
		for (Object key:Weather.defaultProperties.keySet()) {
			propertiesMap.put((String)key, Weather.defaultProperties.getProperty((String)key));
		}
		for ( Map.Entry<String, String>entry:propertiesMap.entrySet() ) {
			sb.append("    <tr><td>").append(entry.getKey()).append("=").append(entry.getValue()).append("</td></tr>");
		}
		sb.append("  </table>\n");
		return sb.toString();
	}

	public String getWeatherKey() {
		return this.weatherKey;
	}

	public String getState() {
		return this.state;
	}

	public String getLocation() {
		return this.location;
	}

	public ScheduleManager getScheduleManager() {
		return this.scheduleManager;
	}

	public FrameManager getFrameManager() {
		return this.frameManager;
	}

	public Map<String,DataFeed>getDataFeeds() {
		return this.dataFeeds;
	}

	public static int getIntegerProperty(String key, int defaultValue) {
		String property = Weather.properties.getProperty(key,"").replaceAll("\\D", "");
		if ( property==null || property.length()==0 ) {
			return defaultValue;
		} else {
			return Integer.valueOf(property);
		}
	}
	
	private class PropertyComparator implements Comparator<String> {

		@Override
		public int compare(String arg0, String arg1) {
			String[] arg0Parts = arg0.split("\\.");
			String[] arg1Parts = arg1.split("\\.");
			int arg0Int = 999999;
			int arg1Int = 999999;
			if ( arg0Parts[0].replaceAll("\\D","").length()>0 ) {
				arg0Int = Integer.valueOf(arg0Parts[0].replaceAll("\\D",""));
			}
			if ( arg1Parts[0].replaceAll("\\D","").length()>0 ) {
				arg1Int = Integer.valueOf(arg1Parts[0].replaceAll("\\D",""));
			}
			if ( arg0Int==arg1Int ) {
				return arg0.compareTo(arg1);
			} else {
				return arg0Int-arg1Int;
			}
		}
		
	}
}
