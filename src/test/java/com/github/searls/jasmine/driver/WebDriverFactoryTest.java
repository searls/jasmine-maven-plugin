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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.Capabilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.openqa.selenium.remote.CapabilityType.SUPPORTS_JAVASCRIPT;

@ExtendWith(MockitoExtension.class)
public class WebDriverFactoryTest {

  @Mock
  private WebDriverConfiguration config;

  @InjectMocks
  private WebDriverFactory factory;

  @BeforeEach
  public void beforeEach() {
    factory = new WebDriverFactory();
  }

  @Test
  public void customDriverIsCreatedWithDefaultConstructorIfNoCapabilitiesConstructorExists() {
    when(config.getWebDriverClassName()).thenReturn(CustomDriverWithDefaultConstructor.class.getName());
    assertThat(factory.createWebDriver(config)).isExactlyInstanceOf(CustomDriverWithDefaultConstructor.class);
  }

  @Test
  public void customDriverIsCreatedWithCapabilitiesIfConstructorExists() {
    when(config.getWebDriverClassName()).thenReturn(CustomDriverWithCapabilities.class.getName());
    assertThat(factory.createWebDriver(config)).isExactlyInstanceOf(CustomDriverWithCapabilities.class);
  }

  @Test
  public void enablesJavascriptOnCustomDriver() throws Exception {
    assertThat(createWebDriverAndReturnCapabilities().is(SUPPORTS_JAVASCRIPT)).isTrue();
  }

  @Test
  public void setsCapabilityFromMap() throws Exception {
    Capability capability = new Capability();
    capability.setName("foo");
    capability.setValue("bar");

    when(config.getWebDriverCapabilities()).thenReturn(ImmutableList.of(capability));

    assertThat(createWebDriverAndReturnCapabilities().getCapability("foo")).isEqualTo("bar");
  }

  private Capabilities createWebDriverAndReturnCapabilities() {
    when(config.getWebDriverClassName()).thenReturn(CustomDriverWithCapabilities.class.getName());
    CustomDriverWithCapabilities driver = (CustomDriverWithCapabilities) factory.createWebDriver(config);
    return driver.getCapabilities();
  }
}
