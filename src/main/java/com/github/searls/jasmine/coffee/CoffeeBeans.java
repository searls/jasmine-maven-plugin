package com.github.searls.jasmine.coffee;

import org.apache.commons.lang.StringEscapeUtils;

public class CoffeeBeans {
	private String coffee;
	private boolean coffeeBareOption;
	
	//private String escapeCoffeeCache;
	
	private static final String BARE_OPTION_ENABLED = "{bare: true}";
	
	public CoffeeBeans(String coffee, boolean coffeeBareOption) {
		this.coffee = coffee;
		this.coffeeBareOption = coffeeBareOption;
	}

	public String getCacheKey() {
		return String.format("Eval#%s,Options#%s", this.escape(), getOptions());
	}
	
	public String escape() {
//		if (this.escapeCoffeeCache == null) {
//			this.escapeCoffeeCache = StringEscapeUtils.escapeJavaScript(this.coffee);
//		}
//		return this.escapeCoffeeCache;
		return StringEscapeUtils.escapeJavaScript(this.coffee);
	}
	
	public String getOptions() {
		return this.coffeeBareOption ? BARE_OPTION_ENABLED : null;
	}
	
	public String createCoffeeScriptFunction() {
		StringBuilder function = new StringBuilder();
		function.append("CoffeeScript.compile(\"");
		function.append(escape());
		function.append("\"");
		if (coffeeBareOption) {
			function.append(", ");
			function.append(BARE_OPTION_ENABLED);
		}
		function.append(");");
		return function.toString();
	}
}