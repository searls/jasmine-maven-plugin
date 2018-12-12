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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openqa.selenium.remote.RemoteWebDriver;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebDriverJasmineObserverTest {

  private static final int TIMEOUT = 2;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private RemoteWebDriver webDriver;

  @InjectMocks
  private WebDriverJasmineObserver subject;

  @Test
  public void itShouldWait() throws Exception {
    boolean debug = false;
    when(webDriver.executeScript(WebDriverJasmineObserver.EXECUTION_FINISHED_SCRIPT)).thenReturn(true);

    subject.waitForRunnerToFinish(webDriver, TIMEOUT, debug);
  }

  @Test
  public void itShouldThrowWhenTimesOut() throws Exception {
    expectedException.expect(IllegalStateException.class);
    expectedException.expectMessage("Timeout occurred.");

    boolean debug = false;
    when(webDriver.executeScript(WebDriverJasmineObserver.EXECUTION_FINISHED_SCRIPT)).thenReturn(false);

    subject.waitForRunnerToFinish(webDriver, TIMEOUT, debug);
  }

  @Test
  public void itShouldLogWhenTimesOutDebugging() throws Exception {
    boolean debug = true;
    when(webDriver.executeScript(WebDriverJasmineObserver.EXECUTION_FINISHED_SCRIPT)).thenReturn(false);

    subject.waitForRunnerToFinish(webDriver, TIMEOUT, debug);
  }
}
