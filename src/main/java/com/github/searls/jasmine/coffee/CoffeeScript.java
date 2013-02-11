package com.github.searls.jasmine.coffee;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.lang3.StringEscapeUtils;

import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.searls.jasmine.io.IOUtilsWrapper;

public class CoffeeScript {

  private static Map<String,String> cache = Collections.synchronizedMap(new WeakHashMap<String,String>());

  private final ThreadLocal<HtmlPage> htmlPage = new ThreadLocal<HtmlPage>() {
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

  private final IOUtilsWrapper ioUtilsWrapper = new IOUtilsWrapper();

  public String compile(String coffee) throws IOException {
    String escapedCoffee = StringEscapeUtils.escapeEcmaScript(coffee);
    return cache.containsKey(escapedCoffee) ? cache.get(escapedCoffee) : compileAndCache(escapedCoffee);
  }

  private String compileAndCache(String input) {
    ScriptResult scriptResult = htmlPage.get().executeJavaScript(String.format("CoffeeScript.compile(\"%s\");", input));
    String result = (String) scriptResult.getJavaScriptResult();
    cache.put(input,result);
    return result;
  }

}
