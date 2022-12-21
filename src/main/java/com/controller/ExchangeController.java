package com.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.business.BusinessCurrency;
import com.entity.Currency;

@Controller
public class ExchangeController {
	BusinessCurrency businessCurrency = new BusinessCurrency();
	
	@RequestMapping("/exchange")
	public String search(ModelMap model, 
			@RequestParam("fromCode") Optional<String> fromCode,
			@RequestParam("toCode") Optional<String> toCode,
			@RequestParam("amount") Optional<String> amount)
	throws IOException, ParseException {
		String amountDouble = amount.orElse("1.0");
		String toCodeString = toCode.orElse("VND");
		String fromCodeString = fromCode.orElse("USD");
		String result = businessCurrency.getRateWithAmount(fromCodeString, toCodeString, Double.valueOf(amountDouble));
		
		
		List<Currency> currencies = businessCurrency.getAllCurrencies();
		String json = businessCurrency.getTop30RatesWithAmountJson(fromCodeString, toCodeString, Double.valueOf(amountDouble));
		model.addAttribute("result", result);
		model.addAttribute("json", json);
		model.addAttribute("symbols", currencies);
		
		model.addAttribute("fromCode", fromCodeString);
		model.addAttribute("toCode", toCodeString);
		model.addAttribute("amount", amountDouble);
		return "index";
	}
}
