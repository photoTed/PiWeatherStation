package com.d2g.mead.conditions;

import java.io.File;
import java.net.URL;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.d2g.mead.weather.DataFeed;
import com.d2g.mead.weather.DataManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ConditionsFeed extends DataFeed {

	public ConditionsFeed(DataManager dataManager) {
		super(dataManager);
		this.name = "conditions";
		setProperties();
	}

	@Override
	public void update(boolean scheduleNext) throws Exception {
		try {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Conditions.class, new ConditionsDeserializer());
			Gson gson = gsonBuilder.create();
			FileUtils.copyURLToFile(new URL(url), new File(filename));
			Conditions conditions = gson.fromJson(dataManager.readUrl("file:" + filename), Conditions.class);
			dataManager.getFrameManager().getConditionsFrame().updateConditions(conditions);
			lastRefreshed = new Date();
			dataManager.notifyListeners(name);
		} finally {
			if ( scheduleNext ) {
				scheduleNextUpdate();
			}
		}
	}

	public Date getNextScheduleDate() {
		return dataManager.getScheduleManager().getDailySchedule(refreshInterval).getNextDate(refreshInterval,false);
	}

}
