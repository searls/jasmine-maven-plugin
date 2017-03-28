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

import com.github.klieber.phantomjs.locate.PhantomJsLocator;
import com.github.klieber.phantomjs.locate.PhantomJsLocatorOptions;
import com.github.klieber.phantomjs.locate.RepositoryDetails;
import com.github.searls.jasmine.mojo.Capability;
import com.google.common.base.MoreObjects;
import org.codehaus.plexus.util.StringUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;

/**
 * Creates a WebDriver for TestMojo using configured properties.
 */
public class WebDriverFactory {
  private boolean debug;
  private String browserVersion;
  private String webDriverClassName;
  private List<Capability> webDriverCapabilities;
  private PhantomJsLocatorOptions phantomJsLocatorOptions;
  private RepositoryDetails repositoryDetails;

  public WebDriverFactory() {
    setWebDriverCapabilities(null);
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public void setBrowserVersion(String browserVersion) {
    this.browserVersion = browserVersion;
  }

  public void setWebDriverClassName(String webDriverClassName) {
    this.webDriverClassName = webDriverClassName;
  }

  public void setWebDriverCapabilities(List<Capability> webDriverCapabilities) {
    this.webDriverCapabilities = MoreObjects.firstNonNull(webDriverCapabilities, Collections.<Capability>emptyList());
  }

  public PhantomJsLocatorOptions getPhantomJsLocatorOptions() {
    return phantomJsLocatorOptions;
  }

  public void setPhantomJsLocatorOptions(PhantomJsLocatorOptions phantomJsLocatorOptions) {
    this.phantomJsLocatorOptions = phantomJsLocatorOptions;
  }

  public RepositoryDetails getRepositoryDetails() {
    return repositoryDetails;
  }

  public void setRepositoryDetails(RepositoryDetails repositoryDetails) {
    this.repositoryDetails = repositoryDetails;
  }

  public WebDriver createWebDriver() throws Exception {
    if (PhantomJSDriver.class.getName().equals(webDriverClassName)) {
      return createPhantomJsWebDriver();
    } else if (HtmlUnitDriver.class.getName().equals(webDriverClassName)) {
      return createHtmlUnitWebDriver();
    } else {
      return createCustomWebDriver();
    }
  }

  @SuppressWarnings("unchecked")
  private Class<? extends WebDriver> getWebDriverClass() throws Exception {
    return (Class<WebDriver>) Class.forName(webDriverClassName);
  }

  private Constructor<? extends WebDriver> getWebDriverConstructor() throws Exception {
    Class<? extends WebDriver> webDriverClass = getWebDriverClass();
    boolean hasCapabilities = !webDriverCapabilities.isEmpty();
    try {
      if (hasCapabilities) {
        return webDriverClass.getConstructor(Capabilities.class);
      }
      return webDriverClass.getConstructor();
    } catch (Exception exception) {
      if (hasCapabilities) {
        return webDriverClass.getConstructor();
      }
      return webDriverClass.getConstructor(Capabilities.class);
    }
  }

  private WebDriver createCustomWebDriver() throws Exception {
    Constructor<? extends WebDriver> constructor = getWebDriverConstructor();
    return constructor.newInstance(getWebDriverConstructorArguments(constructor));
  }

  private Object[] getWebDriverConstructorArguments(Constructor<? extends WebDriver> constructor) {
    if (constructor.getParameterTypes().length == 0) {
      return new Object[0];
    }
    return new Object[]{getCapabilities()};
  }

  private DesiredCapabilities getCapabilities() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    customizeCapabilities(capabilities);
    return capabilities;
  }

  private void customizeCapabilities(DesiredCapabilities capabilities) {
    capabilities.setJavascriptEnabled(true);

    for (Capability capability : webDriverCapabilities) {
      Object value = capability.getValue();
      if (value != null && (!String.class.isInstance(value) || StringUtils.isNotBlank((String) value))) {
        capabilities.setCapability(capability.getName(), capability.getValue());
      } else if (capability.getList() != null && !capability.getList().isEmpty()) {
        capabilities.setCapability(capability.getName(), capability.getList());
      } else if (capability.getMap() != null && !capability.getMap().isEmpty()) {
        capabilities.setCapability(capability.getName(), capability.getMap());
      }
    }
  }

  private WebDriver createHtmlUnitWebDriver() throws Exception {
    DesiredCapabilities capabilities = DesiredCapabilities.htmlUnitWithJs();
    if (StringUtils.isNotBlank(capabilities.getVersion())) {
      capabilities.setVersion(browserVersion.replaceAll("(\\D+)_(\\d.*)?", "$1-$2").replaceAll("_", " ").toLowerCase());
    }
    customizeCapabilities(capabilities);
    return new QuietHtmlUnitDriver(capabilities, debug);
  }

  private WebDriver createPhantomJsWebDriver() throws Exception {
    DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
    customizeCapabilities(capabilities);

    if (capabilities.getCapability("phantomjs.binary.path") == null) {
      PhantomJsLocator locator = new PhantomJsLocator(this.phantomJsLocatorOptions, this.repositoryDetails);
      String phantomJsPath = locator.locate();
      capabilities.setCapability("phantomjs.binary.path", phantomJsPath);
    }

    return new PhantomJSDriver(capabilities);
  }
}
