/*-
 * #%L
 * jasmine-maven-plugin
 * %%
 * Copyright (C) 2010 - 2017 Justin Searls
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.searls.jasmine.coffee;

import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.searls.jasmine.io.IOUtilsWrapper;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class CoffeeScript {

  private static Map<String, String> cache = Collections.synchronizedMap(new WeakHashMap<String, String>());

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
    cache.put(input, result);
    return result;
  }

}
