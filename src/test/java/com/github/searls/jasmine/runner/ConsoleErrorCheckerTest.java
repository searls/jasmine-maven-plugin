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
