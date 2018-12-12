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
import com.github.searls.jasmine.model.ImmutableJasmineResult;
import com.github.searls.jasmine.model.JasmineResult;
import com.github.searls.jasmine.model.Reporter;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@Named
public class SpecRunnerExecutor {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpecRunnerExecutor.class);

  private final IoUtilities ioUtilities;
  private final WebDriverJasmineObserver webDriverWaiter;
  private final ConsoleErrorChecker consoleErrorChecker;

  @Inject
  public SpecRunnerExecutor(IoUtilities ioUtilities,
                            WebDriverJasmineObserver webDriverWaiter,
                            ConsoleErrorChecker consoleErrorChecker) {
    this.ioUtilities = ioUtilities;
    this.webDriverWaiter = webDriverWaiter;
    this.consoleErrorChecker = consoleErrorChecker;
  }

  public JasmineResult execute(final URL runnerUrl,
                               final WebDriver driver,
                               final int timeout,
                               final boolean debug,
                               final String format,
                               final List<Reporter> reporters,
                               final List<FileSystemReporter> fileSystemReporters) {
    try {
      if (!(driver instanceof JavascriptExecutor)) {
        throw new RuntimeException("The provided web driver can't execute JavaScript: " + driver.getClass());
      }
      JavascriptExecutor executor = (JavascriptExecutor) driver;
      driver.get(runnerUrl.toString());
      webDriverWaiter.waitForRunnerToFinish(driver, timeout, debug);

      consoleErrorChecker.checkForConsoleErrors(driver);

      storeFileSystemReports(fileSystemReporters, executor, debug);

      return ImmutableJasmineResult.builder()
        .details(buildReports(reporters, executor, format))
        .build();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      try {
        driver.quit();
      } catch (Exception e) {
        LOGGER.error("There was an exception quitting WebDriver.", e);
      }
    }
  }

  private void storeFileSystemReports(final List<FileSystemReporter> fileSystemReporters,
                                      final JavascriptExecutor executor,
                                      final boolean debug) throws IOException {
    for (FileSystemReporter reporter : fileSystemReporters) {
      ioUtilities.writeStringToFile(
        reporter.getFile(),
        this.buildFileSystemReport(executor, reporter.getReporterFile(), debug)
      );
    }
  }

  private String buildFileSystemReport(final JavascriptExecutor driver,
                                       final File reporter,
                                       final boolean debug) throws IOException {
    final String command = "return fileSystemReporter.report(window.jsApiReporter," + debug + ");";
    return executeReportCommand(driver, reporter, command);
  }

  private String buildReports(final List<Reporter> reporters,
                              final JavascriptExecutor executor,
                              final String format) throws IOException {
    final StringBuilder report = new StringBuilder();
    for (Reporter reporter : reporters) {
      report.append(buildReport(executor, reporter.getReporterFile(), format));
    }
    return report.toString();
  }

  private String buildReport(final JavascriptExecutor driver,
                             final File reporter,
                             final String format) throws IOException {
    final String command = "return jasmineMavenPlugin.printReport(window.jsApiReporter,{format:'" + format + "'});";
    return executeReportCommand(driver, reporter, command);
  }

  private String executeReportCommand(final JavascriptExecutor driver,
                                      final File reporter,
                                      final String command) throws IOException {
    final String script = this.ioUtilities.readFileToString(reporter) + command;
    final Object report = driver.executeScript(script);
    return report.toString();
  }
}
