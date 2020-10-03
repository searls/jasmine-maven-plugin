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

import com.github.searls.jasmine.io.IoUtilities;
import com.github.searls.jasmine.model.FileSystemReporter;
import com.github.searls.jasmine.model.JasmineResult;
import com.github.searls.jasmine.model.Reporter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.URL;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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

    runnerUrl = this.getClass().getResource("/ioUtils.txt");

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
