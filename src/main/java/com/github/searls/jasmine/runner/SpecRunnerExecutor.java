package com.github.searls.jasmine.runner;

import com.github.searls.jasmine.io.FileUtilsWrapper;
import com.github.searls.jasmine.io.IOUtilsWrapper;
import com.github.searls.jasmine.model.JasmineResult;
import org.apache.maven.plugin.logging.Log;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class SpecRunnerExecutor {

  public static final String CREATE_JUNIT_XML = "/lib/createJunitXml.js";

  private final IOUtilsWrapper ioUtilsWrapper;
  private final FileUtilsWrapper fileUtilsWrapper;
  private final WebDriverWaiter webDriverWaiter;
  private final ConsoleErrorChecker consoleErrorChecker;

  public SpecRunnerExecutor(IOUtilsWrapper ioUtilsWrapper, FileUtilsWrapper fileUtilsWrapper, WebDriverWaiter webDriverWaiter, ConsoleErrorChecker consoleErrorChecker) {
    this.ioUtilsWrapper = ioUtilsWrapper;
    this.fileUtilsWrapper = fileUtilsWrapper;
    this.webDriverWaiter = webDriverWaiter;
    this.consoleErrorChecker = consoleErrorChecker;
  }

  public SpecRunnerExecutor() {
    this(new IOUtilsWrapper(), new FileUtilsWrapper(), new WebDriverWaiter(), new ConsoleErrorChecker());
  }


  public JasmineResult execute(URL runnerUrl, File junitXmlReport, WebDriver driver, int timeout, boolean debug, Log log, String format, List<File> reporters) {
    try {
      if (!(driver instanceof JavascriptExecutor)) {
        throw new RuntimeException("The provided web driver can't execute JavaScript: " + driver.getClass());
      }
      JavascriptExecutor executor = (JavascriptExecutor) driver;
      driver.get(runnerUrl.toString());
      webDriverWaiter.waitForRunnerToFinish(driver, timeout, debug, log);

      consoleErrorChecker.checkForConsoleErrors(driver, log);

      JasmineResult jasmineResult = new JasmineResult();
      for (File reporter : reporters) {
        jasmineResult.appendDetails(this.buildReport(executor, reporter, format));
      }
      fileUtilsWrapper.writeStringToFile(junitXmlReport, this.buildJunitXmlReport(executor, debug));

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

  private String buildReport(JavascriptExecutor driver, File reporter, String format) throws IOException {
    String script =
      this.fileUtilsWrapper.readFileToString(reporter) +
        "return jasmineMavenPlugin.printReport(window.jsApiReporter,{format:'" + format + "'});";
    Object report = driver.executeScript(script);
    return report.toString();
  }

  private String buildJunitXmlReport(JavascriptExecutor driver, boolean debug) throws IOException {
    Object junitReport = driver.executeScript(
      this.ioUtilsWrapper.toString(CREATE_JUNIT_XML) +
        "return junitXmlReporter.report(window.jsApiReporter," + debug + ");");
    return junitReport.toString();
  }


}
