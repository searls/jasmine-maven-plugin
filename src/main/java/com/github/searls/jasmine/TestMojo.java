package com.github.searls.jasmine;

import com.github.searls.jasmine.driver.WebDriverFactory;
import com.github.searls.jasmine.format.JasmineResultLogger;
import com.github.searls.jasmine.model.JasmineResult;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.runner.SpecRunnerExecutor;
import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.jetty.server.Server;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.net.URL;

/**
 * @component
 * @goal test
 * @phase test
 */
public class TestMojo extends AbstractServerMojo {

  @Override
  protected void executeJasmine(Server server) throws Exception {
    if (!this.skipTests) {
      try {
        server.start();
        this.getLog().info("Executing Jasmine Specs");
        JasmineResult result = this.executeSpecs(new URL("http://localhost:" + this.getPort()));
        this.logResults(result);
        this.throwAnySpecFailures(result);
      } finally {
        server.stop();
      }
    } else {
      this.getLog().info("Skipping Jasmine Specs");
    }
  }

  @Override
  protected ReporterType getReporterType() {
    return ReporterType.JsApiReporter;
  }

  @Override
  protected String getSpecRunnerFilename() {
    return this.specRunnerHtmlFileName;
  }

  private JasmineResult executeSpecs(URL runner) throws Exception {
    WebDriver driver = this.createDriver();
    JasmineResult result = new SpecRunnerExecutor().execute(
        runner,
        new File(this.jasmineTargetDir, this.junitXmlReportFileName),
        driver,
        this.timeout, this.debug, this.getLog(), this.format);
    return result;
  }

  private WebDriver createDriver() throws Exception {
    WebDriverFactory factory = new WebDriverFactory();
    factory.setWebDriverCapabilities(webDriverCapabilities);
    factory.setWebDriverClassName(webDriverClassName);
    factory.setDebug(debug);
    factory.setBrowserVersion(browserVersion);
    return factory.createWebDriver();
  }


  private void logResults(JasmineResult result) {
    JasmineResultLogger resultLogger = new JasmineResultLogger();
    resultLogger.setLog(this.getLog());
    resultLogger.log(result);
  }

  private void throwAnySpecFailures(JasmineResult result) throws MojoFailureException {
    if (this.haltOnFailure && !result.didPass()) {
      throw new MojoFailureException("There were Jasmine spec failures.");
    }
  }
}
