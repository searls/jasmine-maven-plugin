package com.github.searls.jasmine.coffee;

import java.io.IOException;

import org.apache.commons.lang.StringEscapeUtils;

import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.searls.jasmine.io.IOUtilsWrapper;

public class CoffeeScript {

	private IOUtilsWrapper ioUtilsWrapper = new IOUtilsWrapper();
	
	public String compile(String coffee) throws IOException {
//		WebClient webClient = new WebClient();
//		HtmlPage page = webClient.getPage(WebClient.URL_ABOUT_BLANK);
//		page.executeJavaScript(ioUtilsWrapper.toString("/vendor/js/coffee-script.js"));
//		ScriptResult scriptResult = page.executeJavaScript(String.format("CoffeeScript.compile(\"%s\");", StringEscapeUtils.escapeJavaScript(coffee)));
//		return (String) scriptResult.getJavaScriptResult();
		return "";
	}

}
