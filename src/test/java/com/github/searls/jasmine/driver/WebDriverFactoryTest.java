package com.github.searls.jasmine.driver;

import com.github.searls.jasmine.mojo.Capability;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static org.assertj.core.api.Assertions.assertThat;


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
    assertThat(factory.createWebDriver().getClass()).isEqualTo(QuietHtmlUnitDriver.class);
  }

  @Test
  public void defaultDriverEnablesJavascript() throws Exception {
    HtmlUnitDriver htmlUnitDriver = (HtmlUnitDriver) factory.createWebDriver();

    assertThat(htmlUnitDriver.isJavascriptEnabled()).isTrue();
  }

  @Test
  public void customDriverIsCreatedWithDefaultConstructorIfNoCapabilitiesConstructorExists() throws Exception {
    factory.setWebDriverClassName(CustomDriverWithDefaultConstructor.class.getName());

    assertThat(factory.createWebDriver().getClass()).isEqualTo(CustomDriverWithDefaultConstructor.class);
  }


  @Test
  public void customDriverIsCreatedWithCapabilitiesIfConstructorExists() throws Exception {
    factory.setWebDriverClassName(CustomDriverWithCapabilities.class.getName());

    assertThat(factory.createWebDriver().getClass()).isEqualTo(CustomDriverWithCapabilities.class);
  }

  private Capabilities createWebDriverAndReturnCapabilities() throws Exception {
    factory.setWebDriverClassName(CustomDriverWithCapabilities.class.getName());
    CustomDriverWithCapabilities driver = (CustomDriverWithCapabilities) factory.createWebDriver();
    return driver.capabilities;
  }

  @Test
  public void enablesJavascriptOnCustomDriver() throws Exception {
    assertThat(createWebDriverAndReturnCapabilities().isJavascriptEnabled()).isTrue();
  }

  @Test
  public void setsCapabilityFromMap() throws Exception {
    Capability capability = new Capability();
    capability.setName("foo");
    capability.setValue("bar");
    factory.setWebDriverCapabilities(ImmutableList.of(capability));

    assertThat(createWebDriverAndReturnCapabilities().getCapability("foo")).isEqualTo("bar");
  }
}
