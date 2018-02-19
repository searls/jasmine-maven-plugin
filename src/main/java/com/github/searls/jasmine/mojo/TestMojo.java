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
package com.github.searls.jasmine.mojo;

import com.github.searls.jasmine.config.ImmutableServerConfiguration;
import com.github.searls.jasmine.config.ImmutableWebDriverConfiguration;
import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.config.ServerConfiguration;
import com.github.searls.jasmine.config.WebDriverConfiguration;
import com.github.searls.jasmine.driver.WebDriverFactory;
import com.github.searls.jasmine.format.JasmineResultLogger;
import com.github.searls.jasmine.model.JasmineResult;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.runner.SpecRunnerExecutor;
import com.github.searls.jasmine.server.ResourceHandlerConfigurator;
import com.github.searls.jasmine.server.ServerManager;
import com.google.common.annotations.VisibleForTesting;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * Execute specs using Selenium Web Driver. Uses PhantomJsDriver for head-less execution by default.
 */
@Mojo(name = "test", defaultPhase = LifecyclePhase.TEST, requiresDependencyResolution = ResolutionScope.TEST)
public class TestMojo extends AbstractJasmineMojo {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestMojo.class);

  /**
   * Determines the Selenium WebDriver class we'll use to execute the tests. See the Selenium documentation for more
   * details. The plugin uses <a href="https://github.com/detro/ghostdriver">PhantomJSDriver</a> by default.
   * <br>
   * <p>Some valid examples:</p>
   * <ul>
   * <li>org.openqa.selenium.chrome.ChromeDriver</li>
   * <li>org.openqa.selenium.htmlunit.HtmlUnitDriver</li>
   * <li>org.openqa.selenium.firefox.FirefoxDriver</li>
   * <li>org.openqa.selenium.ie.InternetExplorerDriver</li>
   * </ul>
   * <br>
   * See the webDriverCapabilities property for configuring driver specific properties.
   *
   * @since 1.1.0
   */
  @Parameter(defaultValue = "org.openqa.selenium.chrome.ChromeDriver")
  private String webDriverClassName = ChromeDriver.class.getName();

  /**
   * <p>Web driver capabilities used to initialize a DesiredCapabilities instance when creating a web driver.</p>
   * <br>
   * <p>Capabilities value can be either a String, a List, or a Map.</p>
   * <br>
   * <p>Example:</p>
   * <pre>
   * &lt;webDriverCapabilities&gt;
   *   &lt;capability&gt;
   *     &lt;name&gt;phantomjs.binary.path&lt;/name&gt;
   *     &lt;value&gt;/opt/phantomjs/bin/phantomjs&lt;/value&gt;
   *   &lt;/capability&gt;
   *   &lt;capability&gt;
   *     &lt;name&gt;phantomjs.cli.args&lt;/name&gt;
   *     &lt;list&gt;
   *       &lt;value&gt;--disk-cache=true&lt;/value&gt;
   *       &lt;value&gt;--max-disk-cache-size=256&lt;/value&gt;
   *     &lt;/list&gt;
   *   &lt;/capability&gt;
   *   &lt;capability&gt;
   *     &lt;name&gt;proxy&lt;/name&gt;
   *     &lt;map&gt;
   *       &lt;httpProxy&gt;myproxyserver.com:8000&lt;/httpProxy&gt;
   *     &lt;/map&gt;
   *   &lt;/capability&gt;
   * &lt;/webDriverCapabilities&gt;
   * </pre>
   *
   * @since 1.3.1.1
   */
  @Parameter
  private List<Capability> webDriverCapabilities = Collections.emptyList();

  /**
   * <p>Determines the format that jasmine:test will print to console.</p>
   * <p>Valid options:</p>
   * <ul>
   * <li>"documentation" - (default) - print specs in a nested format</li>
   * <li>"progress" - more terse, with a period for a passed specs and an 'F' for failures (e.g. '...F...')</li>
   * </ul>
   *
   * @since 1.1.0
   */
  @Parameter(defaultValue = "documentation")
  private String format = "documentation";

  /**
   * Keep the server alive after the <code>jasmine:test</code> goal exists.
   * Useful if you need to run further analysis on your tests, like collecting code coverage.
   *
   * @since 1.3.1.0
   */
  @Parameter(property = "keepServerAlive", defaultValue = "false")
  private boolean keepServerAlive = false;

  /**
   * Timeout for spec execution in seconds.
   *
   * @since 1.1.0
   */
  @Parameter(defaultValue = "300")
  private int timeout = 300;

  /**
   * Halt the build on test failure.
   *
   * @since 1.1.0
   */
  @Parameter(property = "haltOnFailure", defaultValue = "true")
  private boolean haltOnFailure = true;

  /**
   * True to increase HtmlUnit output and attempt reporting on specs even if a timeout occurred.
   *
   * @since 1.1.0
   */
  @Parameter(defaultValue = "false")
  private boolean debug = false;

  /**
   * Skip execution of tests.
   *
   * @see <a href="http://maven.apache.org/general.html#skip-test">http://maven.apache.org/general.html#skip-test</a>
   * @since 1.1.0
   */
  @Parameter(property = "skipTests")
  private boolean skipTests = false;

  /**
   * Skip compilation and execution of tests.
   *
   * @see <a href="http://maven.apache.org/general.html#skip-test">http://maven.apache.org/general.html#skip-test</a>
   * @since 1.3.1.3
   */
  @Parameter(property = "maven.test.skip")
  private boolean mvnTestSkip = false;

  /**
   * Skip only jasmine tests
   *
   * @since 1.3.1.3
   */
  @Parameter(property = "skipJasmineTests")
  private boolean skipJasmineTests = false;

  private final WebDriverFactory webDriverFactory;
  private final SpecRunnerExecutor specRunnerExecutor;
  private final JasmineResultLogger jasmineResultLogger;
  private final ResourceHandlerConfigurator resourceHandlerConfigurator;

  @Inject
  public TestMojo(MavenProject mavenProject,
                  ResourceRetriever resourceRetriever,
                  ReporterRetriever reporterRetriever,
                  WebDriverFactory webDriverFactory,
                  SpecRunnerExecutor specRunnerExecutor,
                  JasmineResultLogger jasmineResultLogger,
                  ResourceHandlerConfigurator resourceHandlerConfigurator) {
    super(mavenProject, ReporterType.JsApiReporter, resourceRetriever, reporterRetriever);
    this.webDriverFactory = webDriverFactory;
    this.specRunnerExecutor = specRunnerExecutor;
    this.jasmineResultLogger = jasmineResultLogger;
    this.resourceHandlerConfigurator = resourceHandlerConfigurator;
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (!this.isSkipTests()) {
      super.execute();
    } else {
      LOGGER.info("Skipping Jasmine Specs");
    }
  }

  @Override
  public void run(ServerConfiguration serverConfiguration,
                  JasmineConfiguration configuration) throws Exception {
    ServerManager serverManager = ServerManager.newInstance();
    try {
      int port = serverManager.start(resourceHandlerConfigurator.createHandler(configuration));
      setPortProperty(port);
      LOGGER.info("Executing Jasmine Specs");
      JasmineResult result = this.executeSpecs(
        ImmutableServerConfiguration.copyOf(serverConfiguration).withServerPort(port),
        configuration
      );
      this.logResults(result);
      this.throwAnySpecFailures(result);
    } finally {
      if (!keepServerAlive) {
        serverManager.stop();
      }
    }
  }

  private void setPortProperty(int port) {
    getMavenProject().getProperties().setProperty("jasmine.serverPort", String.valueOf(port));
  }

  private JasmineResult executeSpecs(ServerConfiguration serverConfiguration,
                                     JasmineConfiguration configuration) throws Exception {
    WebDriver driver = this.createDriver();
    return specRunnerExecutor.execute(
      serverConfiguration.getServerURL(),
      driver,
      this.timeout,
      this.debug,
      this.format,
      configuration.getReporters(),
      configuration.getFileSystemReporters()
    );
  }

  private WebDriver createDriver() {
    return webDriverFactory.createWebDriver(getWebDriverConfiguration());
  }

  private void logResults(JasmineResult result) {
    jasmineResultLogger.log(result);
  }

  private void throwAnySpecFailures(JasmineResult result) throws MojoFailureException {
    if (this.haltOnFailure && !result.didPass()) {
      throw new MojoFailureException("There were Jasmine spec failures.");
    }
  }

  private WebDriverConfiguration getWebDriverConfiguration() {
    return ImmutableWebDriverConfiguration.builder()
      .debug(this.debug)
      .webDriverCapabilities(webDriverCapabilities)
      .webDriverClassName(webDriverClassName)
      .build();
  }

  private boolean isSkipTests() {
    return this.skipTests || this.mvnTestSkip || this.skipJasmineTests;
  }

  @VisibleForTesting
  void setSkipTests(boolean skipTests) {
    this.skipTests = skipTests;
  }
}
