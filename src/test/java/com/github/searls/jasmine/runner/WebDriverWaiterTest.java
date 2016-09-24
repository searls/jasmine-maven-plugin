package com.github.searls.jasmine.runner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.remote.RemoteWebDriver;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebDriverWaiterTest {

  private static final int TIMEOUT = 2;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private RemoteWebDriver webDriver;

  @InjectMocks
  private WebDriverWaiter subject;

  @Test
  public void itShouldWait() throws Exception {
    boolean debug = false;
    when(webDriver.executeScript(WebDriverWaiter.EXECUTION_FINISHED_SCRIPT)).thenReturn(true);

    subject.waitForRunnerToFinish(webDriver, TIMEOUT, debug);
  }

  @Test
  public void itShouldThrowWhenTimesOut() throws Exception {
    expectedException.expect(IllegalStateException.class);
    expectedException.expectMessage("Timeout occurred.");

    boolean debug = false;
    when(webDriver.executeScript(WebDriverWaiter.EXECUTION_FINISHED_SCRIPT)).thenReturn(false);

    subject.waitForRunnerToFinish(webDriver, TIMEOUT, debug);
  }

  @Test
  public void itShouldLogWhenTimesOutDebugging() throws Exception {
    boolean debug = true;
    when(webDriver.executeScript(WebDriverWaiter.EXECUTION_FINISHED_SCRIPT)).thenReturn(false);

    subject.waitForRunnerToFinish(webDriver, TIMEOUT, debug);
  }
}
