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
package com.github.searls.jasmine.runner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.remote.RemoteWebDriver;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WebDriverWaiterTest {

  private static final int TIMEOUT = 2;

  @Mock
  private RemoteWebDriver webDriver;

  @InjectMocks
  private WebDriverWaiter subject;

  @Test
  public void itShouldWait() {
    boolean debug = false;
    when(webDriver.executeScript(WebDriverWaiter.EXECUTION_FINISHED_SCRIPT)).thenReturn(true);

    subject.waitForRunnerToFinish(webDriver, TIMEOUT, debug);
  }

  @Test
  public void itShouldThrowWhenTimesOut() {
    when(webDriver.executeScript(WebDriverWaiter.EXECUTION_FINISHED_SCRIPT)).thenReturn(false);

    assertThatExceptionOfType(IllegalStateException.class)
      .isThrownBy(() -> subject.waitForRunnerToFinish(webDriver, TIMEOUT, false))
      .withMessageContaining("Timeout occurred.");
  }

  @Test
  public void itShouldLogWhenTimesOutDebugging() {
    when(webDriver.executeScript(WebDriverWaiter.EXECUTION_FINISHED_SCRIPT)).thenReturn(false);

    subject.waitForRunnerToFinish(webDriver, TIMEOUT, true);
  }
}
