package com.d2g.mead.weather;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

public abstract class DataFeed {

	protected static Logger logger = Logger.getLogger(DataFeed.class);
	
	protected String name;
	protected String filename;
	protected String extension;
	protected Date lastRefreshed;
	protected Date nextScheduledUpdate;
	protected int refreshInterval;
	protected String url;
	protected DataManager dataManager;
	protected Type gsonType;
	protected Timer timer = null;

	public DataFeed(DataManager dataManager) {
		this.dataManager = dataManager;
		extension = ".json";
	}

	public void setProperties() {
		filename = "/tmp/" + name + extension;
		if ( dataManager.getWeatherMaster()!=null ) {
			setMasterUrl(dataManager.getWeatherMaster(), dataManager.getPort());
		} else {
			url = Weather.properties.getProperty(name + ".url","http://api.wunderground.com/api/" + dataManager.getWeatherKey() + "/" + name + "/q/" + dataManager.getState() + "/" + dataManager.getLocation() + ".json");
		}
		refreshInterval = DataManager.getIntegerProperty(name + ".refresh.interval",Integer.parseInt(Weather.defaultProperties.getProperty(name + ".refresh.interval")));
	}

	public void setMasterUrl(String masterIpAddress, int port) {
		url = "http://" + masterIpAddress + ":" + port + "/get/" + name + extension;
	}

	public abstract void update(boolean scheduleNext) throws Exception;

	public String getHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("      <td class=\"tableCell\" ><span class=\"tableCellLeft\">" + name + "</span>"
				+ "<span class=\"tableCellRight\"><a href=\"/refresh/" + name + "\">(refresh now)<a></span></td>\n");
		sb.append("      <td class=\"tableCell\" >").append(lastRefreshed.toString()).append("</td>\n");
		sb.append("      <td class=\"tableCell\" >").append(nextScheduledUpdate.toString()).append("</td>\n");
		sb.append("    </tr>\n");
		sb.append("    <tr>\n");
		sb.append("    <tr>\n");
		return sb.toString();
	}

	protected void scheduleNextUpdate() {
		nextScheduledUpdate = getNextScheduleDate();
		if ( timer!=null ) {
			timer.cancel();
		}
		TimerTask task = new TimerTask() {
			public void run() {
				try {
					update(true);
				} catch (Exception e) {
					logger.error("Error loading " + name + " data feed.",e);
					e.printStackTrace();
				}
			}
		};
		timer = new Timer();
		logger.debug("Next " + name +" update scheduled for: " + nextScheduledUpdate);
		timer.schedule(task, nextScheduledUpdate);
	}
	
	public Date getNextScheduleDate() {
		return dataManager.getScheduleManager().getDailySchedule(refreshInterval).getNextDate(refreshInterval);
	}

	public String getUrl() {
		return this.url;
	}

	public int getUpdateInterval() {
		return this.refreshInterval;
	}

	public String getName() {
		return this.name;
	}

	public Date getLastUpdated() {
		return this.lastRefreshed;
	}
	
	public Date getNextScheduledUpdate() {
		return this.nextScheduledUpdate;
	}

}
