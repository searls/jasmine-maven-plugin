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
import com.github.searls.jasmine.mojo.Capability;
import com.google.common.collect.ImmutableMap;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.codehaus.plexus.util.StringUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.inject.Named;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Creates a WebDriver for TestMojo using configured properties.
 */
@Named
public class WebDriverFactory {

  private static final Map<String, Function<WebDriverConfiguration, WebDriver>> SUPPORTED_DRIVERS =
    ImmutableMap.<String, Function<WebDriverConfiguration, WebDriver>>builder()
      .put(ChromeDriver.class.getName(), WebDriverFactory::createChromeDriver)
      .put(RemoteWebDriver.class.getName(), WebDriverFactory::createRemoteWebDriver)
      .build();

  public WebDriver createWebDriver(WebDriverConfiguration config) {
    return SUPPORTED_DRIVERS
      .getOrDefault(config.getWebDriverClassName(), WebDriverFactory::createCustomWebDriver)
      .apply(config);
  }

  @SuppressWarnings("unchecked")
  private static Class<? extends WebDriver> getWebDriverClass(String className) {
    try {
      return (Class<WebDriver>) Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new WebDriverFactoryException(e);
    }
  }

  private static Constructor<? extends WebDriver> getWebDriverConstructor(Class<? extends WebDriver> webDriverClass,
                                                                          Capabilities options,
                                                                          List<Capability> capabilities) {

    WebDriverManager.getInstance(webDriverClass).setup();

    if (options != null) {
      return getConstructorWithOptions(webDriverClass, options.getClass());
    } else if (!capabilities.isEmpty()) {
      return getConstructorWithCapabilities(webDriverClass);
    } else {
      return getConstructorWithoutCapabilities(webDriverClass);
    }
  }

  private static <E extends WebDriver, T extends Capabilities> Constructor<E> getConstructorWithOptions(Class<E> webDriverClass,
                                                                                                        Class<T> optionsType) {
    try {
      return webDriverClass.getConstructor(optionsType);
    } catch (NoSuchMethodException originalException) {
      return getConstructorWithCapabilities(webDriverClass);
    }
  }

  private static <E extends WebDriver> Constructor<E> getConstructorWithoutCapabilities(Class<E> webDriverClass) {
    try {
      return webDriverClass.getConstructor();
    } catch (NoSuchMethodException originalException) {
      try {
        return webDriverClass.getConstructor(Capabilities.class);
      } catch (NoSuchMethodException e) {
        throw new WebDriverFactoryException(originalException);
      }
    }
  }

  private static <E extends WebDriver> Constructor<E> getConstructorWithCapabilities(Class<E> webDriverClass) {

    try {
      return webDriverClass.getConstructor(Capabilities.class);
    } catch (NoSuchMethodException originalException) {
      try {
        return webDriverClass.getConstructor();
      } catch (NoSuchMethodException e) {
        throw new WebDriverFactoryException(originalException);
      }
    }
  }

  private static WebDriver createCustomWebDriver(WebDriverConfiguration config) {
    Class<? extends WebDriver> webDriverClass = getWebDriverClass(config.getWebDriverClassName());
    return createCustomWebDriver(webDriverClass, config.getWebDriverOptions(), config.getWebDriverCapabilities());
  }

  private static WebDriver createCustomWebDriver(Class<? extends WebDriver> webDriverClass,
                                                 MutableCapabilities options,
                                                 List<Capability> capabilities) {
    Constructor<? extends WebDriver> constructor = getWebDriverConstructor(webDriverClass, options, capabilities);
    try {
      return constructor.newInstance(getWebDriverConstructorArguments(constructor, options, capabilities));
    } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
      throw new WebDriverFactoryException(e);
    }
  }

  private static Object[] getWebDriverConstructorArguments(Constructor<? extends WebDriver> constructor,
                                                           MutableCapabilities options,
                                                           List<Capability> customCapabilities) {
    if (constructor.getParameterTypes().length == 0) {
      return new Object[0];
    }

    return new Object[]{getCapabilities(options, customCapabilities)};
  }

  private static MutableCapabilities getCapabilities(MutableCapabilities options, List<Capability> customCapabilities) {
    return customizeCapabilities(options != null ? options : new DesiredCapabilities(), customCapabilities);
  }

  private static <E extends MutableCapabilities> E customizeCapabilities(E capabilities, WebDriverConfiguration config) {
    return customizeCapabilities(capabilities, config.getWebDriverCapabilities());
  }

  private static <E extends MutableCapabilities> E customizeCapabilities(E capabilities, List<Capability> customCapabilities) {
    for (Capability capability : customCapabilities) {
      Object value = capability.getValue();
      if (value != null && (!String.class.isInstance(value) || StringUtils.isNotBlank((String) value))) {
        capabilities.setCapability(capability.getName(), capability.getValue());
      } else if (capability.getList() != null && !capability.getList().isEmpty()) {
        capabilities.setCapability(capability.getName(), capability.getList());
      } else if (capability.getMap() != null && !capability.getMap().isEmpty()) {
        capabilities.setCapability(capability.getName(), capability.getMap());
      }
    }

    return capabilities;
  }

  private static WebDriver createChromeDriver(WebDriverConfiguration config) {
    WebDriverManager.getInstance(ChromeDriver.class).setup();

    MutableCapabilities options = config.getWebDriverOptions();

    ChromeOptions chromeOptions;
    if (options instanceof ChromeOptions) {
      chromeOptions = (ChromeOptions) options;
    } else if (options != null) {
      chromeOptions = new ChromeOptions().merge(options);
    } else {
      chromeOptions = new ChromeOptions()
        .setHeadless(true)
        .addArguments("--no-sandbox")
        .addArguments("--disable-dev-shm-usage");
    }

    return new ChromeDriver(customizeCapabilities(chromeOptions, config));
  }

  private static WebDriver createRemoteWebDriver(WebDriverConfiguration config) {
    return new RemoteWebDriver(
      config.getRemoteWebDriverUrl(),
      getCapabilities(config.getWebDriverOptions(), config.getWebDriverCapabilities())
    );
  }
}
