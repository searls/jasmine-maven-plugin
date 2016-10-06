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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConsoleErrorCheckerTest {

  private static final String ERROR = "Bad to the Bone!";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private WebDriver webDriver;


  @Mock
  private WebElement headWithErrors;


  @Mock
  private WebElement headWithoutErrors;

  @InjectMocks
  private ConsoleErrorChecker subject;

  @Before
  public void setUp() throws Exception {
    subject = new ConsoleErrorChecker();
    when(headWithErrors.getAttribute("jmp_jserror")).thenReturn(ERROR);
    when(headWithoutErrors.getAttribute("jmp_jserro")).thenReturn("");
  }

  @Test
  public void shouldPassWhenNoErrors() throws Exception {
    when(webDriver.findElement(By.tagName("head"))).thenReturn(headWithoutErrors);

    subject.checkForConsoleErrors(webDriver);
  }

  @Test
  public void shouldThrowWhenErrors() throws Exception {
    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("There were javascript console errors.");

    when(webDriver.findElement(By.tagName("head"))).thenReturn(headWithErrors);

    subject.checkForConsoleErrors(webDriver);
  }
}
