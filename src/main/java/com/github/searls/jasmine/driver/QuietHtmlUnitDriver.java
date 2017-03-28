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
package com.github.searls.jasmine.driver;

import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * The default web driver - overridden to tweak a few things.
 */
public class QuietHtmlUnitDriver extends HtmlUnitDriver {

  private final boolean debug;

  public QuietHtmlUnitDriver(Capabilities capabilities, boolean debug) {
    super(capabilities);
    this.debug = debug;
    this.setJavascriptEnabled(true);
  }

  @Override
  protected WebClient modifyWebClient(WebClient client) {
    client.setAjaxController(new NicelyResynchronizingAjaxController());

    //Disables stuff like this "com.gargoylesoftware.htmlunit.IncorrectnessListenerImpl notify WARNING: Obsolete content type encountered: 'text/javascript'."
    if (!this.debug) {
      client.setIncorrectnessListener(new IncorrectnessListener() {
        @Override
        public void notify(String message, Object origin) {
        }
      });
    }
    return client;
  }
}
