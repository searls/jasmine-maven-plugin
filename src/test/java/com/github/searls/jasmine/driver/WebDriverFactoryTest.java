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
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.remote.CapabilityType.SUPPORTS_JAVASCRIPT;

@RunWith(MockitoJUnitRunner.Silent.class)
public class WebDriverFactoryTest {

  @Mock
  private WebDriverConfiguration config;

  @Mock
  private DriverManagerAdapter driverManagerAdapter;

  @InjectMocks
  private WebDriverFactory factory = new WebDriverFactory();

  @Test
  public void createsQuietHtmlUnitDriver() throws Exception {
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
    assertThat(createWebDriverAndReturnCapabilities().is(SUPPORTS_JAVASCRIPT)).isTrue();
  }

  @Test
  public void webDriverManagerIsUsedForChromeDriverIfDownloadIsEnabled() throws Exception {
    ChromeDriver chromeDriver = mock(ChromeDriver.class);
    when(config.getWebDriverClassName()).thenReturn(ChromeDriver.class.getName());
    when(config.isWebDriverDownloadEnabled()).thenReturn(true);

    when(driverManagerAdapter.createChromeDriver(any(ChromeOptions.class))).thenReturn(chromeDriver);

    assertThat(factory.createWebDriver(config)).isInstanceOf(ChromeDriver.class);
    verify(driverManagerAdapter).setupDriver(ChromeDriver.class);
  }

  @Test
  public void webDriverManagerIsNotUsedForChromeDriverIfDownloadIsNotEnabled() throws Exception {
    ChromeDriver chromeDriver = mock(ChromeDriver.class);
    when(config.getWebDriverClassName()).thenReturn(ChromeDriver.class.getName());
    when(config.isWebDriverDownloadEnabled()).thenReturn(false);

    when(driverManagerAdapter.createChromeDriver(any(ChromeOptions.class))).thenReturn(chromeDriver);
    doThrow(new RuntimeException("Should not call setupDriver")).when(driverManagerAdapter).setupDriver(any());

    assertThat(factory.createWebDriver(config)).isInstanceOf(ChromeDriver.class);
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
    return driver.getCapabilities();
  }
}
