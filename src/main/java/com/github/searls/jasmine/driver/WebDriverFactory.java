package com.github.searls.jasmine.driver;

import com.github.klieber.phantomjs.locate.PhantomJsLocator;
import com.github.klieber.phantomjs.locate.PhantomJsLocatorOptions;
import com.github.klieber.phantomjs.locate.RepositoryDetails;
import com.github.searls.jasmine.config.WebDriverConfiguration;
import com.github.searls.jasmine.mojo.Capability;
import org.codehaus.plexus.util.StringUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.inject.Named;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Creates a WebDriver for TestMojo using configured properties.
 */
@Named
public class WebDriverFactory {

  private static final String PHANTOMJS_BINARY_PATH = "phantomjs.binary.path";

  public WebDriver createWebDriver(WebDriverConfiguration config) throws Exception {
    String className = config.getWebDriverClassName();
    WebDriver driver;
    if (PhantomJSDriver.class.getName().equals(className)) {
      driver = createPhantomJsWebDriver(
        config.getPhantomJsLocatorOptions(),
        config.getRepositoryDetails(),
        config.getWebDriverCapabilities()
      );
    } else if (HtmlUnitDriver.class.getName().equals(className)) {
      driver = createHtmlUnitWebDriver(config.getBrowserVersion(), config.getWebDriverCapabilities(), config.isDebug());
    } else {
      driver = createCustomWebDriver(className, config.getWebDriverCapabilities());
    }
    return driver;
  }

  @SuppressWarnings("unchecked")
  private Class<? extends WebDriver> getWebDriverClass(String className) throws Exception {
    return (Class<WebDriver>) Class.forName(className);
  }

  private Constructor<? extends WebDriver> getWebDriverConstructor(String className, List<Capability> capabilities) throws Exception {
    Class<? extends WebDriver> webDriverClass = getWebDriverClass(className);
    boolean hasCapabilities = !capabilities.isEmpty();
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

  private WebDriver createCustomWebDriver(String className, List<Capability> capabilities) throws Exception {
    Constructor<? extends WebDriver> constructor = getWebDriverConstructor(className, capabilities);
    return constructor.newInstance(getWebDriverConstructorArguments(constructor, capabilities));
  }

  private Object[] getWebDriverConstructorArguments(Constructor<? extends WebDriver> constructor,
                                                    List<Capability> customCapabilities) {
    if (constructor.getParameterTypes().length == 0) {
      return new Object[0];
    }
    return new Object[]{getCapabilities(customCapabilities)};
  }

  private DesiredCapabilities getCapabilities(List<Capability> customCapabilities) {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    customizeCapabilities(capabilities, customCapabilities);
    return capabilities;
  }

  private void customizeCapabilities(DesiredCapabilities capabilities, List<Capability> customCapabilities) {
    capabilities.setJavascriptEnabled(true);

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
  }

  private WebDriver createHtmlUnitWebDriver(String browserVersion,
                                            List<Capability> customCapabilities,
                                            boolean debug) throws Exception {
    DesiredCapabilities capabilities = DesiredCapabilities.htmlUnitWithJs();
    if (StringUtils.isNotBlank(capabilities.getVersion())) {
      capabilities.setVersion(browserVersion.replaceAll("(\\D+)_(\\d.*)?", "$1-$2").replaceAll("_", " ").toLowerCase());
    }
    customizeCapabilities(capabilities, customCapabilities);
    return new QuietHtmlUnitDriver(capabilities, debug);
  }

  private WebDriver createPhantomJsWebDriver(PhantomJsLocatorOptions options,
                                             RepositoryDetails repositoryDetails,
                                             List<Capability> customCapabilities) throws Exception {
    DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
    customizeCapabilities(capabilities, customCapabilities);

    if (capabilities.getCapability(PHANTOMJS_BINARY_PATH) == null) {
      PhantomJsLocator locator = new PhantomJsLocator(options, repositoryDetails);
      String phantomJsPath = locator.locate();
      capabilities.setCapability(PHANTOMJS_BINARY_PATH, phantomJsPath);
    }

    return new PhantomJSDriver(capabilities);
  }
}
