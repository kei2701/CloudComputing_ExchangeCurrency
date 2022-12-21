package com.business;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GetJSON {
	public static JsonObject getJsonObject(String url) throws IOException {
		String url_str = url;

		URL urll = new URL(url_str);
		HttpURLConnection request = (HttpURLConnection) urll.openConnection();
		request.connect();

		JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(request.getInputStream())).getAsJsonObject();
		return jsonObject;
	}
}
