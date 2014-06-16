package com.d2g.mead.conditions;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;


public class ConditionsDeserializer implements JsonDeserializer<Conditions> {

	@Override
	public Conditions deserialize(JsonElement json, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		Gson gson = new Gson();
		return gson.fromJson(jsonObject.get("current_observation"),Conditions.class);
	}

}
