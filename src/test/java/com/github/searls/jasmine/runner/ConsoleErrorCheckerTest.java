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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.hamcrest.Matchers.allOf;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConsoleErrorCheckerTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  WebDriver webDriver;
  @Mock
  Log log;

  @Mock
  WebElement headWithErrors;
  private String error = "Bad to the Bone!";
  @Mock
  WebElement headWithoutErrors;

  ConsoleErrorChecker subject;

  @Before
  public void setUp() throws Exception {
    subject = new ConsoleErrorChecker();
    when(headWithErrors.getAttribute("jmp_jserror")).thenReturn(error);
    when(headWithoutErrors.getAttribute("jmp_jserro")).thenReturn("");
  }

  @Test
  public void shouldPassWhenNoErrors() throws Exception {
    when(webDriver.findElement(By.tagName("head"))).thenReturn(headWithoutErrors);

    subject.checkForConsoleErrors(webDriver, log);
  }

  @Test
  public void shouldThrowWhenErrors() throws Exception {
    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("There were javascript console errors.");

    when(webDriver.findElement(By.tagName("head"))).thenReturn(headWithErrors);

    subject.checkForConsoleErrors(webDriver, log);

    verify(log).warn(argThat(allOf(new StringContains("JavaScript Console Errors:"), new StringContains(error))));
  }
}
