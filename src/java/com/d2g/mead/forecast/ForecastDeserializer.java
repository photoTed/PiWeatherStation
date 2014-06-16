package com.d2g.mead.forecast;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ForecastDeserializer implements JsonDeserializer<Forecast> {

	@Override
	public Forecast deserialize(JsonElement json, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		//System.out.println("deserialize element " + jsonObject.get("forecast"));
		Gson gson = new Gson();
		return gson.fromJson(jsonObject.get("forecast"),Forecast.class);
	}

}
