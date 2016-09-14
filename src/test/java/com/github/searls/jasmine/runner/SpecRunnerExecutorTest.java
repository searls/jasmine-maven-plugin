package com.github.searls.jasmine.runner;

import com.github.searls.jasmine.io.FileUtilsWrapper;
import com.github.searls.jasmine.model.FileSystemReporter;
import com.github.searls.jasmine.model.JasmineResult;
import com.github.searls.jasmine.model.Reporter;
import org.apache.maven.plugin.logging.Log;
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

  @Mock
  private FileUtilsWrapper fileUtilsWrapper;
  @Mock
  private WebDriverWaiter webDriverWaiter;
  @Mock
  private ConsoleErrorChecker consoleErrorChecker;

  private URL runnerUrl;
  @Mock
  private File junitXmlReport;
  @Mock
  private File junitXmlReporter;
  @Mock
  private RemoteWebDriver webDriver;
  private int timeout = 2;
  private boolean debug = false;
  @Mock
  private Log log;
  private String format = null;
  @Mock
  File reporter;
  private String report = "report";

  private SpecRunnerExecutor subject;

  @Before
  public void setUp() throws Exception {
    subject = new SpecRunnerExecutor(fileUtilsWrapper, webDriverWaiter, consoleErrorChecker);

    runnerUrl = PowerMockito.mock(URL.class);

    when(fileUtilsWrapper.readFileToString(reporter)).thenReturn("reporter");
    when(fileUtilsWrapper.readFileToString(junitXmlReporter)).thenReturn("reporter");
    when(webDriver.executeScript(contains("reporter"))).thenReturn(report);
  }

  @Test
  public void shouldExecute() throws Exception {
    JasmineResult result = subject.execute(runnerUrl, webDriver, timeout, debug, log, format, Collections.singletonList(new Reporter(reporter)), Collections.singletonList(new FileSystemReporter(junitXmlReport, junitXmlReporter)));

    verify(webDriver).get(runnerUrl.toString());
    verify(webDriverWaiter).waitForRunnerToFinish(webDriver, timeout, debug, log);
    verify(fileUtilsWrapper).writeStringToFile(junitXmlReport, report);
    assertThat(result).isNotNull();
    assertThat(result.getDetails()).contains(report);
  }
}
