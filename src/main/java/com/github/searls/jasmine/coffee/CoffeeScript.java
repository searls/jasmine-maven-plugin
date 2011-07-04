package com.github.searls.jasmine.coffee;

import java.io.IOException;

import org.apache.commons.lang.StringEscapeUtils;

import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.searls.jasmine.io.IOUtilsWrapper;

public class CoffeeScript {

	private ThreadLocal<HtmlPage> htmlPage = new ThreadLocal<HtmlPage>() {
		@Override
		protected HtmlPage initialValue() {
			MockWebConnection webConnection = new MockWebConnection();
			WebClient webClient = new WebClient();
			webClient.setWebConnection(webConnection);
			try {
				HtmlPage page = webClient.getPage(WebClient.URL_ABOUT_BLANK);
				page.executeJavaScript(ioUtilsWrapper.toString("/vendor/js/coffee-script.js"));
				return page;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	};
	
	private IOUtilsWrapper ioUtilsWrapper = new IOUtilsWrapper();
	
	public String compile(String coffee) throws IOException {
		ScriptResult scriptResult = htmlPage.get().executeJavaScript(String.format("CoffeeScript.compile(\"%s\");", StringEscapeUtils.escapeJavaScript(coffee)));
		return (String) scriptResult.getJavaScriptResult();
	}

}
