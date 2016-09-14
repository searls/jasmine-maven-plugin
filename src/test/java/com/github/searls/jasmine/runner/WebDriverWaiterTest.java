package com.github.searls.jasmine.runner;

import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.remote.RemoteWebDriver;

import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.matches;
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

    verify(log).warn(matches("Attempted to wait.*" + timeout + " seconds.*"));
    verify(log).warn(contains("Debug mode:"));
  }
}
