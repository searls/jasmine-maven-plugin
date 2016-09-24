package com.github.searls.jasmine.runner;

import com.github.searls.jasmine.io.IoUtilities;
import com.github.searls.jasmine.model.FileSystemReporter;
import com.github.searls.jasmine.model.JasmineResult;
import com.github.searls.jasmine.model.Reporter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.net.URL;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(URL.class)
public class SpecRunnerExecutorTest {

  private static final int TIMEOUT = 2;
  private static final boolean DEBUG = false;
  private static final String FORMAT = null;
  private static final String REPORT = "REPORT";

  private URL runnerUrl;

  @Mock
  private IoUtilities ioUtilities;

  @Mock
  private WebDriverWaiter webDriverWaiter;

  @Mock
  private ConsoleErrorChecker consoleErrorChecker;

  @Mock
  private File junitXmlReport;

  @Mock
  private File junitXmlReporter;

  @Mock
  private RemoteWebDriver webDriver;

  @Mock
  private File reporter;

  private SpecRunnerExecutor subject;

  @Before
  public void setUp() throws Exception {
    subject = new SpecRunnerExecutor(ioUtilities, webDriverWaiter, consoleErrorChecker);

    runnerUrl = PowerMockito.mock(URL.class);

    when(ioUtilities.readFileToString(reporter)).thenReturn("reporter");
    when(ioUtilities.readFileToString(junitXmlReporter)).thenReturn("reporter");
    when(webDriver.executeScript(contains("reporter"))).thenReturn(REPORT);
  }

  @Test
  public void shouldExecute() throws Exception {
    JasmineResult result = subject.execute(
      runnerUrl,
      webDriver,
      TIMEOUT,
      DEBUG,
      FORMAT,
      Collections.singletonList(new Reporter(reporter)),
      Collections.singletonList(new FileSystemReporter(junitXmlReport, junitXmlReporter))
    );

    verify(webDriver).get(runnerUrl.toString());
    verify(webDriverWaiter).waitForRunnerToFinish(webDriver, TIMEOUT, DEBUG);
    verify(ioUtilities).writeStringToFile(junitXmlReport, REPORT);
    assertThat(result).isNotNull();
    assertThat(result.getDetails()).contains(REPORT);
  }
}
