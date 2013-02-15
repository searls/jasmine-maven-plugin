package com.github.searls.jasmine;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.github.searls.jasmine.format.JasmineResultLogger;
import com.github.searls.jasmine.io.scripts.TargetDirScriptResolver;
import com.github.searls.jasmine.model.JasmineResult;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.runner.SpecRunnerExecutor;
import com.github.searls.jasmine.runner.SpecRunnerHtmlGenerator;
import com.github.searls.jasmine.runner.SpecRunnerHtmlGeneratorFactory;
import com.google.common.base.Objects;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Map;

/**
 * @component
 * @goal test
 * @phase test
 * @execute phase="jasmine-process-test-resources"
 */
public class TestMojo extends AbstractJasmineMojo {

  public void run() throws Exception {
    if(!skipTests) {
      getLog().info("Executing Jasmine Specs");
      File runnerFile = writeSpecRunnerToOutputDirectory();
      JasmineResult result = executeSpecs(runnerFile);
      logResults(result);
      throwAnySpecFailures(result);
    } else {
      getLog().info("Skipping Jasmine Specs");
    }
  }

  private File writeSpecRunnerToOutputDirectory() throws IOException {

    SpecRunnerHtmlGenerator generator = new SpecRunnerHtmlGeneratorFactory().create(ReporterType.JsApiReporter, this, new TargetDirScriptResolver(this));

    String html = generator.generate();

    getLog().debug("Writing out Spec Runner HTML " + html + " to directory " + jasmineTargetDir);
    File runnerFile = new File(jasmineTargetDir,specRunnerHtmlFileName);
    FileUtils.writeStringToFile(runnerFile, html);
    return runnerFile;
  }

  private JasmineResult executeSpecs(File runnerFile) throws Exception {
    return new SpecRunnerExecutor().execute(
        runnerFile.toURI().toURL(),
        new File(jasmineTargetDir, junitXmlReportFileName),
        createDriver(),
        timeout,
        debug,
        getLog(),
        format
    );
  }

  @SuppressWarnings("unchecked")
  private Class<? extends WebDriver> getWebDriverClass() throws Exception {
    return (Class<WebDriver>) Class.forName(webDriverClassName);
  }

  private Constructor<? extends WebDriver> getWebDriverConstructor() throws Exception {
    Class<? extends WebDriver> webDriverClass = getWebDriverClass();
    try {
      return webDriverClass.getConstructor(Capabilities.class);
    } catch (Exception exception) {
      return webDriverClass.getConstructor();
    }
  }

  private Object[] getWebDriverConstructorArguments(Constructor<? extends WebDriver> constructor) throws Exception {
    if (constructor.getParameterTypes().length == 0) {
      return new Object[0];
    } else {
      return new Object[] {getCapabilities()};
    }
  }

  private Map<String, String> getWebDriverCapabilities() {
    return Objects.firstNonNull(webDriverCapabilities, Collections.<String, String>emptyMap());
  }

  private Capabilities getCapabilities() throws Exception {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setJavascriptEnabled(true);
    for (Map.Entry<String, String> entry : getWebDriverCapabilities().entrySet()) {
      capabilities.setCapability(entry.getKey(), entry.getValue());
    }
    return capabilities;
  }

  private WebDriver createDriver() throws Exception {
    if (HtmlUnitDriver.class.getName().equals(webDriverClassName)) {
      return createDefaultWebDriver();
    } else {
      return createCustomWebDriver();
    }
  }

  private BrowserVersion getBrowserVersion() throws Exception {
      return (BrowserVersion) BrowserVersion.class.getField(browserVersion).get(BrowserVersion.class);
  }

  private WebDriver createDefaultWebDriver() throws Exception {
    return new HtmlUnitDriver(getBrowserVersion()) {
      {
        setJavascriptEnabled(true);
      }
      protected WebClient modifyWebClient(WebClient client) {
        client.setAjaxController(new NicelyResynchronizingAjaxController());

        //Disables stuff like this "com.gargoylesoftware.htmlunit.IncorrectnessListenerImpl notify WARNING: Obsolete content type encountered: 'text/javascript'."
        if (!debug) {
          client.setIncorrectnessListener(new IncorrectnessListener() {
            public void notify(String arg0, Object arg1) {
            }
          });
        }
        return client;
      }
    };
  }

  private WebDriver createCustomWebDriver() throws Exception {
    Constructor<? extends WebDriver> constructor = getWebDriverConstructor();
    return constructor.newInstance(getWebDriverConstructorArguments(constructor));
  }

  private void logResults(JasmineResult result) {
    JasmineResultLogger resultLogger = new JasmineResultLogger();
    resultLogger.setLog(getLog());
    resultLogger.log(result);
  }

  private void throwAnySpecFailures(JasmineResult result) throws MojoFailureException {
    if(haltOnFailure && !result.didPass()) {
      throw new MojoFailureException("There were Jasmine spec failures.");
    }
  }
}
