package com.github.searls.jasmine.driver;

import com.github.searls.jasmine.config.WebDriverConfiguration;
import com.github.searls.jasmine.mojo.Capability;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebDriverFactoryTest {

  @Mock
  private WebDriverConfiguration config;

  @InjectMocks
  private WebDriverFactory factory;

  @Before
  public void setUp() {
    factory = new WebDriverFactory();
  }

  @Test
  public void createsQuietHtmlUnitDriver() throws Exception {
    when(config.getBrowserVersion()).thenReturn("FIREFOX_3_6");
    when(config.getWebDriverClassName()).thenReturn(HtmlUnitDriver.class.getName());
    assertThat(factory.createWebDriver(config)).isExactlyInstanceOf(QuietHtmlUnitDriver.class);
  }

  @Test
  public void customDriverIsCreatedWithDefaultConstructorIfNoCapabilitiesConstructorExists() throws Exception {
    when(config.getWebDriverClassName()).thenReturn(CustomDriverWithDefaultConstructor.class.getName());
    assertThat(factory.createWebDriver(config)).isExactlyInstanceOf(CustomDriverWithDefaultConstructor.class);
  }


  @Test
  public void customDriverIsCreatedWithCapabilitiesIfConstructorExists() throws Exception {
    when(config.getWebDriverClassName()).thenReturn(CustomDriverWithCapabilities.class.getName());
    assertThat(factory.createWebDriver(config)).isExactlyInstanceOf(CustomDriverWithCapabilities.class);
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
    when(config.getWebDriverCapabilities()).thenReturn(ImmutableList.of(capability));

    assertThat(createWebDriverAndReturnCapabilities().getCapability("foo")).isEqualTo("bar");
  }

  private Capabilities createWebDriverAndReturnCapabilities() throws Exception {
    when(config.getWebDriverClassName()).thenReturn(CustomDriverWithCapabilities.class.getName());
    CustomDriverWithCapabilities driver = (CustomDriverWithCapabilities) factory.createWebDriver(config);
    return driver.capabilities;
  }
}
