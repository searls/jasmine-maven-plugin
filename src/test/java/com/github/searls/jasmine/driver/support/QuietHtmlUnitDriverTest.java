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
package com.github.searls.jasmine.driver.support;

import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.github.searls.jasmine.driver.support.QuietHtmlUnitDriver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class QuietHtmlUnitDriverTest {

  @Mock
  private WebClient mockWebClient;

  @Mock
  private DesiredCapabilities capabilities;

  private QuietHtmlUnitDriver driver;
  private boolean debug = false;

  private void createDriver() {
    driver = new QuietHtmlUnitDriver(capabilities, debug);
  }

  @Test
  public void enablesJavascript() {
    createDriver();

    assertThat(driver.isJavascriptEnabled()).isTrue();
  }

  private void modifyWebClient() {
    createDriver();
    driver.modifyWebClient(mockWebClient);
  }

  @Test
  public void installsNewAjaxController() throws Exception {
    modifyWebClient();

    verify(mockWebClient).setAjaxController(isA(NicelyResynchronizingAjaxController.class));
  }

  @Test
  public void overridesIncorrectnessListenerToSuppressOutput() {
    modifyWebClient();

    verify(mockWebClient).setIncorrectnessListener(isA(IncorrectnessListener.class));
  }

  @Test
  public void doesNotOverrideIncorrectnessListenerWhenDebugFlagIsSet() {
    debug = true;
    modifyWebClient();

    verify(mockWebClient, never()).setIncorrectnessListener(isA(IncorrectnessListener.class));
  }
}
