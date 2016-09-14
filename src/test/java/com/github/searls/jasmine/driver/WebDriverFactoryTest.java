package com.github.searls.jasmine.driver;

import com.github.searls.jasmine.mojo.Capability;
import com.google.common.collect.ImmutableList;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;


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
    Assertions.assertThat(factory.createWebDriver().getClass()).isEqualTo(QuietHtmlUnitDriver.class);
  }

  @Test
  public void defaultDriverEnablesJavascript() throws Exception {
    HtmlUnitDriver htmlUnitDriver = (HtmlUnitDriver) factory.createWebDriver();

    Assertions.assertThat(htmlUnitDriver.isJavascriptEnabled()).isTrue();
  }

  @Test
  public void customDriverIsCreatedWithDefaultConstructorIfNoCapabilitiesConstructorExists() throws Exception {
    factory.setWebDriverClassName(CustomDriverWithDefaultConstructor.class.getName());

    Assertions.assertThat(factory.createWebDriver().getClass()).isEqualTo(CustomDriverWithDefaultConstructor.class);
  }


  @Test
  public void customDriverIsCreatedWithCapabilitiesIfConstructorExists() throws Exception {
    factory.setWebDriverClassName(CustomDriverWithCapabilities.class.getName());

    Assertions.assertThat(factory.createWebDriver().getClass()).isEqualTo(CustomDriverWithCapabilities.class);
  }

  private Capabilities createWebDriverAndReturnCapabilities() throws Exception {
    factory.setWebDriverClassName(CustomDriverWithCapabilities.class.getName());
    CustomDriverWithCapabilities driver = (CustomDriverWithCapabilities) factory.createWebDriver();
    return driver.capabilities;
  }

  @Test
  public void enablesJavascriptOnCustomDriver() throws Exception {
    Assertions.assertThat(createWebDriverAndReturnCapabilities().isJavascriptEnabled()).isTrue();
  }

  @Test
  public void setsCapabilityFromMap() throws Exception {
    Capability capability = new Capability();
    capability.setName("foo");
    capability.setValue("bar");
    factory.setWebDriverCapabilities(ImmutableList.of(capability));

    Assertions.assertThat(createWebDriverAndReturnCapabilities().getCapability("foo")).isEqualTo("bar");
  }
}
