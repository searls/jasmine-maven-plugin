package com.github.searls.jasmine.runner;

import com.github.searls.jasmine.io.FileUtilsWrapper;
import com.github.searls.jasmine.model.FileSystemReporter;
import com.github.searls.jasmine.model.JasmineResult;
import com.github.searls.jasmine.model.Reporter;
import org.apache.maven.plugin.logging.Log;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class SpecRunnerExecutor {

  private final FileUtilsWrapper fileUtilsWrapper;
  private final WebDriverWaiter webDriverWaiter;
  private final ConsoleErrorChecker consoleErrorChecker;

  public SpecRunnerExecutor(FileUtilsWrapper fileUtilsWrapper, WebDriverWaiter webDriverWaiter, ConsoleErrorChecker consoleErrorChecker) {
    this.fileUtilsWrapper = fileUtilsWrapper;
    this.webDriverWaiter = webDriverWaiter;
    this.consoleErrorChecker = consoleErrorChecker;
  }

  public SpecRunnerExecutor() {
    this(new FileUtilsWrapper(), new WebDriverWaiter(), new ConsoleErrorChecker());
  }


  public JasmineResult execute(final URL runnerUrl, final WebDriver driver, final int timeout, final boolean debug, final Log log, final String format, final List<Reporter> reporters, final List<FileSystemReporter> fileSystemReporters) {
    try {
      if (!(driver instanceof JavascriptExecutor)) {
        throw new RuntimeException("The provided web driver can't execute JavaScript: " + driver.getClass());
      }
      JavascriptExecutor executor = (JavascriptExecutor) driver;
      driver.get(runnerUrl.toString());
      webDriverWaiter.waitForRunnerToFinish(driver, timeout, debug, log);

      consoleErrorChecker.checkForConsoleErrors(driver, log);

      storeFileSystemReports(fileSystemReporters, executor, debug);

      JasmineResult jasmineResult = new JasmineResult();
      jasmineResult.setDetails(buildReports(reporters, executor, format));
      return jasmineResult;

    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      try {
        driver.quit();
      } catch (Exception e) {
        log.error("There was an exception quitting WebDriver.", e);
      }
    }
  }

  private void storeFileSystemReports(final List<FileSystemReporter> fileSystemReporters, final JavascriptExecutor executor, final boolean debug) throws IOException {
    for (FileSystemReporter reporter : fileSystemReporters) {
      fileUtilsWrapper.writeStringToFile(reporter.file, this.buildFileSystemReport(executor, reporter.reporterFile, debug));
    }
  }

  private String buildFileSystemReport(final JavascriptExecutor driver, final File reporter, final boolean debug) throws IOException {
    final String command = "return fileSystemReporter.report(window.jsApiReporter," + debug + ");";
    return executeReportCommand(driver, reporter, command);
  }

  private String buildReports(final List<Reporter> reporters, final JavascriptExecutor executor, final String format) throws IOException {
    final StringBuilder report = new StringBuilder();
    for (Reporter reporter : reporters) {
      report.append(buildReport(executor, reporter.reporterFile, format));
    }
    return report.toString();
  }

  private String buildReport(final JavascriptExecutor driver, final File reporter, final String format) throws IOException {
    final String command = "return jasmineMavenPlugin.printReport(window.jsApiReporter,{format:'" + format + "'});";
    return executeReportCommand(driver, reporter, command);
  }

  private String executeReportCommand(final JavascriptExecutor driver, final File reporter, final String command) throws IOException {
    final String script = this.fileUtilsWrapper.readFileToString(reporter) + command;
    final Object report = driver.executeScript(script);
    return report.toString();
  }


}
