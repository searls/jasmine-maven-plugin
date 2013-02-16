package com.github.searls.jasmine.driver;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.google.common.base.Objects;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Map;

/**
 * Creates a WebDriver for TestMojo using configured properties.
 */
public class WebDriverFactory {
  private boolean debug;
  private String browserVersion;
  private String webDriverClassName;
  private Map<String, String> webDriverCapabilities;

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

  public void setWebDriverCapabilities(Map<String, String> webDriverCapabilities) {
    this.webDriverCapabilities = Objects.firstNonNull(webDriverCapabilities, Collections.<String, String>emptyMap());
  }

  public WebDriver createWebDriver() throws Exception {
    if (HtmlUnitDriver.class.getName().equals(webDriverClassName)) {
      return createDefaultWebDriver();
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
    try {
      return webDriverClass.getConstructor(Capabilities.class);
    } catch (Exception exception) {
      return webDriverClass.getConstructor();
    }
  }

  private WebDriver createCustomWebDriver() throws Exception {
    Constructor<? extends WebDriver> constructor = getWebDriverConstructor();
    return constructor.newInstance(getWebDriverConstructorArguments(constructor));
  }

  private Object[] getWebDriverConstructorArguments(Constructor<? extends WebDriver> constructor) {
    if (constructor.getParameterTypes().length == 0) {
      return new Object[0];
    } else
      return new Object[] {getCapabilities()};
  }

  private Capabilities getCapabilities() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setJavascriptEnabled(true);
    for (Map.Entry<String, String> entry : webDriverCapabilities.entrySet()) {
      capabilities.setCapability(entry.getKey(), entry.getValue());
    }
    return capabilities;
  }


  private BrowserVersion getBrowserVersion() throws Exception {
    return (BrowserVersion) BrowserVersion.class.getField(browserVersion).get(BrowserVersion.class);
  }

  private WebDriver createDefaultWebDriver() throws Exception {
    return new QuietHtmlUnitDriver(getBrowserVersion(), debug);
  }
}
