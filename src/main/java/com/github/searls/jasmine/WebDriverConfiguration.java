package com.github.searls.jasmine;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Creates a WebDriver according to the properties configured.
 */
class WebDriverConfiguration {
  private final String webDriverClassName;
  private final String remoteWebDriverUrl;
  private final String browserVersion;
  private final boolean debug;

  WebDriverConfiguration(String webDriverClassName, String remoteWebDriverUrl, String browserVersion, boolean debug) {
    this.webDriverClassName = webDriverClassName;
    this.remoteWebDriverUrl = remoteWebDriverUrl;
    this.browserVersion = browserVersion;
    this.debug = debug;
  }

  WebDriver createWebDriver() {
    if (remoteWebDriverUrl != null) {
      return createRemoteWebDriver();
    } else if (webDriverClassName.equals(HtmlUnitDriver.class.getName())) {
      return createDefaultWebDriver();
    } else {
      return createCustomWebDriver();
    }
  }

  private WebDriver createRemoteWebDriver() {
    try {
      DesiredCapabilities capabilities = new DesiredCapabilities();
      capabilities.setJavascriptEnabled(true);
      return new RemoteWebDriver(new URL(remoteWebDriverUrl), capabilities);
    } catch (MalformedURLException e) {
      throw new RuntimeException("Malformed remoteWebDriverUrl: " + remoteWebDriverUrl);
    }
  }

  private WebDriver createCustomWebDriver() {
    try {
      return (WebDriver) Class.forName(webDriverClassName).getConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Couldn't instantiate " + webDriverClassName, e);
    }
  }

  private WebDriver createDefaultWebDriver() {
    // We have extra configuration to do to the HtmlUnitDriver
    BrowserVersion htmlUnitBrowserVersion;
    try {
      htmlUnitBrowserVersion = (BrowserVersion) BrowserVersion.class.getField(browserVersion).get(BrowserVersion.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    HtmlUnitDriver driver = new HtmlUnitDriver(htmlUnitBrowserVersion) {
      protected WebClient modifyWebClient(WebClient client) {
        client.setAjaxController(new NicelyResynchronizingAjaxController());

        //Disables stuff like this "com.gargoylesoftware.htmlunit.IncorrectnessListenerImpl notify WARNING: Obsolete content type encountered: 'text/javascript'."
        if (!debug) {
          client.setIncorrectnessListener(new IncorrectnessListener() {
            public void notify(String arg0, Object arg1) {
            }
          });
        }
        return client;
      }
    };
    driver.setJavascriptEnabled(true);
    return driver;
  }
}
