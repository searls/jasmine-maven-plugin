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

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConsoleErrorCheckerTest {

  private static final String ERROR = "Bad to the Bone!";

  @Mock
  private MockWebDriver webDriver;

  @InjectMocks
  private ConsoleErrorChecker subject;

  @Before
  public void setUp() {
    subject = new ConsoleErrorChecker();
  }

  @Test
  public void shouldPassWhenNoErrors() {
    when(webDriver.executeScript(anyString())).thenReturn(Collections.emptyList());
    subject.checkForConsoleErrors(webDriver);
  }

  @Test
  public void shouldThrowWhenErrors() {
    when(webDriver.executeScript(anyString())).thenReturn(Collections.singletonList(ERROR));

    Assertions.assertThatExceptionOfType(RuntimeException.class)
      .isThrownBy(() -> subject.checkForConsoleErrors(webDriver))
      .withMessageContaining("There were javascript console errors.");
  }

  interface MockWebDriver extends WebDriver, JavascriptExecutor {

  }
}
