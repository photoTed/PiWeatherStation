package com.d2g.mead.conditions;

import java.io.File;
import java.net.URL;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.d2g.mead.weather.DataFeed;
import com.d2g.mead.weather.DataManager;
import com.d2g.mead.weather.Weather;

public class RadarFeed extends DataFeed {

	public RadarFeed(DataManager dataManager) {
		super(dataManager);
		this.name="radar";
		this.extension=".gif";
		setProperties();
		if ( dataManager.getWeatherMaster()==null ) {
			url = Weather.properties.getProperty("radar.url","http://api.wunderground.com/api/" + dataManager.getWeatherKey() + "/animatedradar/q/" + dataManager.getState() + "/" + dataManager.getLocation() +
					".gif?timelabel=1&timelabel.x=8&timelabel.y=40&num=9&delay=75&width=" + 
					dataManager.getFrameManager().getConditionsFrame().getRadarWidth() + 
					"&height="+ dataManager.getFrameManager().getConditionsFrame().getRadarHeight());
		}
	}

	@Override
	public void update(boolean scheduleNext) throws Exception {
		try {
			logger.debug("loading radar image from " + url);
			FileUtils.copyURLToFile(new URL(url), new File(filename));
			dataManager.getFrameManager().getConditionsFrame().updateRadarImage(new URL("file:" + filename));
			lastRefreshed = new Date();
			dataManager.notifyListeners(name);
		} finally {
			if ( scheduleNext ) {
				scheduleNextUpdate();
			}
		}
	}

}
