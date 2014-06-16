package com.d2g.mead.conditions;

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.d2g.mead.weather.DataFeed;
import com.d2g.mead.weather.DataManager;
import com.d2g.mead.weather.ScheduleManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AstronomyFeed extends DataFeed {
	
	public AstronomyFeed(DataManager dataManager) {
		super(dataManager);
		this.name = "astronomy";
		setProperties();
		gsonType = new TypeToken<Astronomy>() {}.getType();
	}

	@Override
	public void update(boolean scheduleNext) throws Exception {
		try {
			Gson gson = new Gson();
			FileUtils.copyURLToFile(new URL(url), new File(filename));
			Astronomy astronomy = gson.fromJson(dataManager.readUrl("file:" + filename), gsonType);
			dataManager.getFrameManager().getConditionsFrame().updateAstronomy(astronomy);
			lastRefreshed = new Date();
			dataManager.notifyListeners(name);
		} finally {
			if ( scheduleNext ) {
				scheduleNextUpdate();
			}
		}
	}

	public Date getNextScheduleDate() {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		now.add(Calendar.HOUR, 24);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, ScheduleManager.RANDOMIZER);
		now.set(Calendar.HOUR_OF_DAY, 4);
		//now.add(Calendar.MILLISECOND, ScheduleManager.RANDOMIZER);
	    return now.getTime();
	}

}
