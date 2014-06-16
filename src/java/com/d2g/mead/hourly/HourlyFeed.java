package com.d2g.mead.hourly;

import java.io.File;
import java.net.URL;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.d2g.mead.weather.DataFeed;
import com.d2g.mead.weather.DataManager;
import com.google.gson.Gson;

public class HourlyFeed extends DataFeed {

	public HourlyFeed(DataManager dataManager) {
		super(dataManager);
		this.name="hourly";
		setProperties();
	}

	@Override
	public void update(boolean scheduleNext) throws Exception {
		if ( dataManager.getFrameManager().getHourlyFrame()==null ) {
			return;
		}
		try {
			Gson gson = new Gson();
			FileUtils.copyURLToFile(new URL(url), new File(filename));
			Hourly hourly = gson.fromJson(dataManager.readUrl("file:" + filename), Hourly.class);
			dataManager.getFrameManager().getHourlyFrame().updateComponents(hourly.getHourly_forecast());
			lastRefreshed = new Date();
			dataManager.notifyListeners(name);
		} finally {
			if ( scheduleNext ) {
				scheduleNextUpdate();
			}
		}
	}

}
