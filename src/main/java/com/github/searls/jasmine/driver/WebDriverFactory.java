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

import com.github.searls.jasmine.config.WebDriverConfiguration;
import com.github.searls.jasmine.driver.support.ChromeWebDriverSupport;
import com.github.searls.jasmine.driver.support.CustomWebDriverSupport;
import com.github.searls.jasmine.driver.support.HtmlUnitWebDriverSupport;
import com.github.searls.jasmine.driver.support.WebDriverSupport;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import javax.inject.Named;
import java.util.Map;

/**
 * Creates a WebDriver for TestMojo using configured properties.
 */
@Named
public class WebDriverFactory {

  private static final Map<String, WebDriverSupport> SUPPORTED_DRIVERS =
    ImmutableMap.<String, WebDriverSupport>builder()
      .put(ChromeDriver.class.getName(), new ChromeWebDriverSupport())
      .put(HtmlUnitDriver.class.getName(), new HtmlUnitWebDriverSupport())
      .build();

  public WebDriver createWebDriver(WebDriverConfiguration config) {
    return SUPPORTED_DRIVERS
      .getOrDefault(config.getWebDriverClassName(), new CustomWebDriverSupport())
      .create(config);
  }
}
