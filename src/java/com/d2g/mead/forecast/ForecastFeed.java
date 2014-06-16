package com.d2g.mead.forecast;

import java.io.File;
import java.net.URL;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.d2g.mead.weather.DataFeed;
import com.d2g.mead.weather.DataManager;
import com.d2g.mead.weather.Weather;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ForecastFeed extends DataFeed {

	public ForecastFeed(DataManager dataManager) {
		super(dataManager);
		this.name = "forecast";
		setProperties();
		if ( dataManager.getWeatherMaster()==null ) {
			url = Weather.properties.getProperty(name + ".url","http://api.wunderground.com/api/" + dataManager.getWeatherKey() + "/forecast10day/q/" + dataManager.getState() + "/" + dataManager.getLocation() + ".json");
		}
	}

	@Override
	public void update(boolean scheduleNext) throws Exception {
		try {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Forecast.class, new ForecastDeserializer());
			Gson gson = gsonBuilder.create();
			FileUtils.copyURLToFile(new URL(url), new File(filename));
			Forecast forecast = gson.fromJson(dataManager.readUrl("file:" + filename), Forecast.class);
			forecast.load();
			dataManager.getFrameManager().getConditionsFrame().updateForecast(forecast.getSimpleForecast().getForecastday());
			dataManager.getFrameManager().getForecastFrame().updateComponents(forecast.getSimpleForecast().getForecastday());
			lastRefreshed = new Date();
			dataManager.notifyListeners(name);
		} finally {
			if ( scheduleNext ) {
				scheduleNextUpdate();
			}
		}
	}

}
