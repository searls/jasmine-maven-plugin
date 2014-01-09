package com.github.searls.jasmine.mojo;

import java.io.File;
import java.net.URL;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.eclipse.jetty.server.Server;
import org.openqa.selenium.WebDriver;

import com.github.searls.jasmine.NullLog;
import com.github.searls.jasmine.driver.WebDriverFactory;
import com.github.searls.jasmine.format.JasmineResultLogger;
import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.model.JasmineResult;
import com.github.searls.jasmine.runner.CreatesRunner;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.runner.SpecRunnerExecutor;
import com.github.searls.jasmine.server.ResourceHandlerConfigurator;
import com.github.searls.jasmine.server.ServerManager;

/**
 * Execute specs using Selenium Web Driver. Uses HtmlUnitDriver for head-less execution by default.
 */
@Mojo(name="test",defaultPhase=LifecyclePhase.TEST)
public class TestMojo extends AbstractJasmineMojo {

  private final RelativizesFilePaths relativizesFilePaths;

  public TestMojo() {
    this.relativizesFilePaths = new RelativizesFilePaths();
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if(!this.isSkipTests()) {
      super.execute();
    } else {
      this.getLog().info("Skipping Jasmine Specs");
    }
  }

  @Override
  public void run() throws Exception {
    ServerManager serverManager = this.getServerManager();
    try {
      int port = serverManager.start();
      setPortProperty(port);
      this.getLog().info("Executing Jasmine Specs");
      JasmineResult result = this.executeSpecs(new URL(this.uriScheme+"://" + this.serverHostname + ":" + port));
      this.logResults(result);
      this.throwAnySpecFailures(result);
    } finally {
      if (!keepServerAlive) {
        serverManager.stop();
      }
    }
  }

  private ServerManager getServerManager() throws MojoExecutionException {
    Log log = this.debug ? this.getLog() : new NullLog();

    CreatesRunner createsRunner = new CreatesRunner(
        this,
        log,
        this.specRunnerHtmlFileName,
        ReporterType.JsApiReporter);

    ResourceHandlerConfigurator configurator = new ResourceHandlerConfigurator(
        this,
        this.relativizesFilePaths,
        createsRunner);

    return new ServerManager(new Server(), getConnector(), configurator);
  }

  private void setPortProperty(int port) {
    this.mavenProject.getProperties().setProperty("jasmine.serverPort", String.valueOf(port));
  }
  private JasmineResult executeSpecs(URL runner) throws Exception {
    WebDriver driver = this.createDriver();
    JasmineResult result = new SpecRunnerExecutor().execute(
        runner,
        new File(this.jasmineTargetDir,this.junitXmlReportFileName),
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
    if(this.haltOnFailure && !result.didPass()) {
      throw new MojoFailureException("There were Jasmine spec failures.");
    }
  }
}
