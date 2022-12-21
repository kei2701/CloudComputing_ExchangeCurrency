package com.business;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.entity.Currency;
import com.entity.CurrencyChart;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.model.CurrencyChartModel;

public class BusinessCurrency {
	public List<Currency> getAllCurrencies() throws IOException {
		JsonObject jsonObject = GetJSON.getJsonObject("https://api.exchangerate.host/symbols");
		
		JsonObject array = jsonObject.get("symbols").getAsJsonObject();
		
		Set<Map.Entry<String, JsonElement>> entries = array.entrySet();// will return members of your object
		List<Currency> currencies = new ArrayList<>();
		for (Map.Entry<String, JsonElement> entry : entries) {
			Gson gson = new Gson();
			Currency currency = gson.fromJson(entry.getValue(), Currency.class);
			currencies.add(currency);
		}
		return currencies;

	}
	public String getRateWithAmount(String fromCode, String toCode, Double amount ) throws IOException {
		if (amount == null)
			amount = 1.0;
		String url = String.format("https://api.exchangerate.host/convert?from=%1$s&to=%2$s&amount=%3$s", fromCode, toCode, amount.toString());
		JsonObject jsonObject = GetJSON.getJsonObject(url);
		return jsonObject.get("result").getAsString();
	}
	
	public List<CurrencyChart> getTop30RatesWithAmount(String fromCode, String toCode, Double amount ) throws IOException, ParseException {
		if (amount == null)
			amount = 1.0;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String today = formatter.format(new Date());
		
	    LocalDate dateBefore30Days = LocalDate.now(ZoneId.systemDefault()).minusDays(365);
	    String ago =formatter.format(Date.from(dateBefore30Days.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
	    
	    String url = String.format("https://api.exchangerate.host/timeseries?start_date=%1$s&end_date=%2$s&base=%3$s&amount=%4$s&symbols=%5$s"
	    							, ago, today, fromCode, amount.toString(), toCode);
	    JsonObject jsonObject = GetJSON.getJsonObject(url);
	    JsonObject rateObjects = jsonObject.get("rates").getAsJsonObject();
	    
	    Set<Map.Entry<String, JsonElement>> entries = rateObjects.entrySet();// will return members of your object
		List<CurrencyChart> currencyCharts = new ArrayList<>();
		for (Map.Entry<String, JsonElement> entry : entries) {
			try {
				Double rate = entry.getValue().getAsJsonObject().get(toCode).getAsDouble();
				Date date = new SimpleDateFormat("yyyy-MM-dd").parse(entry.getKey());  
				currencyCharts.add(new CurrencyChart(date, rate));
			} catch (Exception e) {
				// TODO: handle exception
			}			
		}
		return currencyCharts;
	}
	
	public String getTop30RatesWithAmountJson(String fromCode, String toCode, Double amount ) throws IOException, ParseException {
		List<CurrencyChart> currencyCharts = getTop30RatesWithAmount(fromCode, toCode, amount);
		List<CurrencyChartModel> currencyChartModels = new ArrayList<>();
		for(CurrencyChart currencyChart: currencyCharts) {
			currencyChartModels.add(new CurrencyChartModel(currencyChart.getX().getTime(), currencyChart.getY()));
		}
		Gson gson = new Gson();
		return gson.toJson(currencyChartModels);
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		
	}
}
