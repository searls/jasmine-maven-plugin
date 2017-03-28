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

import org.apache.maven.plugin.logging.Log;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.remote.RemoteWebDriver;

import static org.hamcrest.Matchers.allOf;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebDriverWaiterTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  RemoteWebDriver webDriver;
  int timeout = 2;
  @Mock
  Log log;

  WebDriverWaiter subject;

  @Before
  public void setUp() throws Exception {
    subject = new WebDriverWaiter();
  }

  @Test
  public void itShouldWait() throws Exception {
    boolean debug = false;
    when(webDriver.executeScript(WebDriverWaiter.EXECUTION_FINISHED_SCRIPT)).thenReturn(true);

    subject.waitForRunnerToFinish(webDriver, timeout, debug, log);
  }

  @Test
  public void itShouldThrowWhenTimesOut() throws Exception {
    expectedException.expect(IllegalStateException.class);
    expectedException.expectMessage("Timeout occurred.");

    boolean debug = false;
    when(webDriver.executeScript(WebDriverWaiter.EXECUTION_FINISHED_SCRIPT)).thenReturn(false);

    subject.waitForRunnerToFinish(webDriver, timeout, debug, log);
  }

  @Test
  public void itShouldLogWhenTimesOutDebugging() throws Exception {
    boolean debug = true;
    when(webDriver.executeScript(WebDriverWaiter.EXECUTION_FINISHED_SCRIPT)).thenReturn(false);

    subject.waitForRunnerToFinish(webDriver, timeout, debug, log);

    verify(log).warn(argThat(allOf(new StringContains("Attempted to wait"), new StringContains(timeout + " seconds"))));
    verify(log).warn(contains("Debug mode:"));
  }
}
