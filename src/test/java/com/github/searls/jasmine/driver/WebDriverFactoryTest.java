package com.github.searls.jasmine.driver;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static org.junit.Assert.*;


public class WebDriverFactoryTest {

  private WebDriverFactory factory;

  @Before
  public void setUp() {
    factory = new WebDriverFactory();
    factory.setBrowserVersion("FIREFOX_3_6");
    factory.setWebDriverClassName(HtmlUnitDriver.class.getName());
  }

  @Test
  public void defaultDriverIsCustomHtmlUnitDriver() throws Exception {
    assertEquals(QuietHtmlUnitDriver.class, factory.createWebDriver().getClass());
  }

  @Test
  public void defaultDriverEnablesJavascript() throws Exception {
    HtmlUnitDriver htmlUnitDriver = (HtmlUnitDriver) factory.createWebDriver();

    assertTrue(htmlUnitDriver.isJavascriptEnabled());
  }

  @Test
  public void customDriverIsCreatedWithDefaultConstructorIfNoCapabilitiesConstructorExists() throws Exception {
    factory.setWebDriverClassName(CustomDriverWithDefaultConstructor.class.getName());

    assertEquals(CustomDriverWithDefaultConstructor.class, factory.createWebDriver().getClass());
  }


  @Test
  public void customDriverIsCreatedWithCapabilitiesIfConstructorExists() throws Exception {
    factory.setWebDriverClassName(CustomDriverWithCapabilities.class.getName());

    assertEquals(CustomDriverWithCapabilities.class, factory.createWebDriver().getClass());
  }

  private Capabilities createWebDriverAndReturnCapabilities() throws Exception {
    factory.setWebDriverClassName(CustomDriverWithCapabilities.class.getName());
    CustomDriverWithCapabilities driver = (CustomDriverWithCapabilities) factory.createWebDriver();
    return driver.capabilities;
  }

  @Test
  public void enablesJavascriptOnCustomDriver() throws Exception {
    assertTrue(createWebDriverAndReturnCapabilities().isJavascriptEnabled());
  }

  @Test
  public void setsCapabilityFromMap() throws Exception {
    factory.setWebDriverCapabilities(ImmutableMap.of("foo", "bar"));

    assertEquals("bar", createWebDriverAndReturnCapabilities().getCapability("foo"));
  }
}
