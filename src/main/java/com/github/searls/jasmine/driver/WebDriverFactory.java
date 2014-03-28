package com.github.searls.jasmine.driver;

import com.github.searls.jasmine.mojo.Capability;
import com.google.common.base.Objects;
import org.codehaus.plexus.util.StringUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Creates a WebDriver for TestMojo using configured properties.
 */
public class WebDriverFactory {
  private boolean debug;
  private String browserVersion;
  private String webDriverClassName;
  private String remoteWebDriverUrl;
  private List<Capability> webDriverCapabilities;

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

  public void setRemoteWebDriverUrl(String remoteWebDriverUrl) {
    this.remoteWebDriverUrl = remoteWebDriverUrl;
  }

  public void setWebDriverCapabilities(List<Capability> webDriverCapabilities) {
    this.webDriverCapabilities = Objects.firstNonNull(webDriverCapabilities, Collections.<Capability>emptyList());
  }

  public WebDriver createWebDriver() throws Exception {
      
    if (remoteWebDriverUrl != null) {
          return createRemoteWebDriver();
    }      
    else if (HtmlUnitDriver.class.getName().equals(webDriverClassName)) {
      return createDefaultWebDriver();
    }
    else {
      return createCustomWebDriver();
    }
  }

    WebDriver createRemoteWebDriver()
    {
        // If the user set remoteWebDriverUrl, webDriverClassName must be either RemoteWebDriver or null
        boolean ok = webDriverClassName == null || RemoteWebDriver.class.getName().equals(webDriverClassName);
        if (!ok)
        {
            throw new RuntimeException("You can't use 'remoteWebDriverUrl' with drivers other than RemoteWebDriver.");
        }

        // In case it's null
        webDriverClassName = RemoteWebDriver.class.getName(); // do we have to?
        
        return new RemoteWebDriver(toUrl(remoteWebDriverUrl), getCapabilities());
    }

    URL toUrl(String url)
    {
        try
        {
            return new URL(url);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
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
    } else
      return new Object[] {getCapabilities()};
  }

  private DesiredCapabilities getCapabilities() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setJavascriptEnabled(true);

    for (Capability capability : webDriverCapabilities) {
      if (StringUtils.isNotBlank(capability.getValue())) {
        capabilities.setCapability(capability.getName(),capability.getValue());
      } else if (capability.getList() != null && !capability.getList().isEmpty()) {
        capabilities.setCapability(capability.getName(),capability.getList());
      } else if (capability.getMap() != null && !capability.getMap().isEmpty()) {
        capabilities.setCapability(capability.getName(),capability.getMap());
      }
    }

    return capabilities;
  }

  private WebDriver createDefaultWebDriver() throws Exception {
    DesiredCapabilities capabilities = getCapabilities();
    if (StringUtils.isBlank(capabilities.getBrowserName())) {
      capabilities.setBrowserName(BrowserType.HTMLUNIT);
    }
    if (StringUtils.isBlank(capabilities.getVersion())) {
      capabilities.setVersion(browserVersion.replaceAll("(\\D+)_(\\d.*)?", "$1-$2").replaceAll("_", " ").toLowerCase());
    }
    return new QuietHtmlUnitDriver(getCapabilities(), debug);
  }
}
