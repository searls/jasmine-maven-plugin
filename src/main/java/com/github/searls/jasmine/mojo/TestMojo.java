package com.github.searls.jasmine.mojo;

import com.github.klieber.phantomjs.locate.PhantomJsLocatorOptions;
import com.github.klieber.phantomjs.locate.RepositoryDetails;
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
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

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
   * <li>org.openqa.selenium.htmlunit.HtmlUnitDriver</li>
   * <li>org.openqa.selenium.phantomjs.PhantomJSDriver</li>
   * <li>org.openqa.selenium.firefox.FirefoxDriver</li>
   * <li>org.openqa.selenium.ie.InternetExplorerDriver</li>
   * </ul>
   * <br>
   * See the webDriverCapabilities property for configuring driver specific properties.
   *
   * @since 1.1.0
   */
  @Parameter(defaultValue = "org.openqa.selenium.phantomjs.PhantomJSDriver")
  private String webDriverClassName = "org.openqa.selenium.phantomjs.PhantomJSDriver";

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
   * <p>Determines the browser and version profile that HtmlUnit will simulate. This setting does nothing if the plugin
   * is configured not to use HtmlUnit. This maps 1-to-1 with the public static instances found in
   * {@link com.gargoylesoftware.htmlunit.BrowserVersion}.</p>
   * <br>
   * <p>Some valid examples: CHROME, FIREFOX_17, INTERNET_EXPLORER_9, INTERNET_EXPLORER_10</p>
   *
   * @since 1.1.0
   * @deprecated Use the webDriverCapabilities parameter instead.
   */
  @Parameter(defaultValue = "FIREFOX_17")
  @Deprecated
  private String browserVersion = "FIREFOX_17";

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
   * <p>Configure which version of PhantomJS should be used and how it should be found. The core of the
   * <a href="http://klieber.github.io/phantomjs-maven-plugin">phantomjs-maven-plugin</a> is used to provide this
   * functionality and this parameter should match the configuration of the
   * <a href="http://kylelieber.com/phantomjs-maven-plugin/install-mojo.html">phantomjs-maven-plugin install</a> goal.</p>
   * <br>
   * <p>Default Options:</p>
   * <pre>
   * &lt;phantomjs&gt;
   *   &lt;version&gt;2.0.0&lt;/version&gt;
   *   &lt;checkSystemPath&gt;true&lt;/checkSystemPath&gt;
   *   &lt;enforceVersion&gt;true&lt;/enforceVersion&gt;
   *   &lt;source&gt;REPOSITORY&lt;/source&gt;
   *   &lt;baseUrl&gt;&lt;/baseUrl&gt;
   *   &lt;outputDirectory&gt;target/phantomjs&lt;/outputDirectory&gt;
   * &lt;/phantomjs&gt;
   * </pre>
   *
   * @since 2.0
   */
  @Parameter(property = "phantomjs", defaultValue = "${phantomJs}")
  private PhantomJsOptions phantomjs = new PhantomJsOptions();

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

  @Parameter(
    defaultValue = "${repositorySystemSession}",
    readonly = true
  )
  private RepositorySystemSession repositorySystemSession = null;

  @Parameter(
    defaultValue = "${project.remoteProjectRepositories}",
    readonly = true
  )
  private List<RemoteRepository> remoteRepositories = null;

  @Parameter(
    defaultValue = "${session}",
    readonly = true
  )
  private MavenSession mavenSession = null;

  private final RepositorySystem repositorySystem;
  private final WebDriverFactory webDriverFactory;
  private final SpecRunnerExecutor specRunnerExecutor;
  private final JasmineResultLogger jasmineResultLogger;
  private final ResourceHandlerConfigurator resourceHandlerConfigurator;

  @Inject
  public TestMojo(MavenProject mavenProject,
                  ResourceRetriever resourceRetriever,
                  ReporterRetriever reporterRetriever,
                  RepositorySystem repositorySystem,
                  WebDriverFactory webDriverFactory,
                  SpecRunnerExecutor specRunnerExecutor,
                  JasmineResultLogger jasmineResultLogger,
                  ResourceHandlerConfigurator resourceHandlerConfigurator) {
    super(mavenProject, ReporterType.JsApiReporter, resourceRetriever, reporterRetriever);
    this.repositorySystem = repositorySystem;
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

  private WebDriver createDriver() throws Exception {
    configure(mavenSession.getUserProperties());
    return webDriverFactory.createWebDriver(getWebDriverConfiguration());
  }

  private void configure(Properties properties) {

    phantomjs.setVersion(
      properties.getProperty("phantomjs.version", phantomjs.getVersion())
    );

    phantomjs.setSource(
      PhantomJsLocatorOptions.Source.valueOf(
        properties.getProperty("phantomjs.source", phantomjs.getSource().toString())
      )
    );

    phantomjs.setOutputDirectory(
      new File(properties.getProperty("phantomjs.outputDirectory", phantomjs.getOutputDirectory().toString()))
    );

    phantomjs.setBaseUrl(
      properties.getProperty("phantomjs.baseUrl", phantomjs.getBaseUrl())
    );

    phantomjs.setCheckSystemPath(
      configureBoolean(properties, "phantomjs.checkSystemPath", phantomjs.isCheckSystemPath())
    );

    phantomjs.setEnforceVersion(
      properties.getProperty("phantomjs.enforceVersion", phantomjs.getEnforceVersion())
    );
  }

  private boolean configureBoolean(Properties properties, String property, boolean defaultValue) {
    return Boolean.parseBoolean(properties.getProperty(property, Boolean.toString(defaultValue)));
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
      .browserVersion(this.browserVersion)
      .phantomJsLocatorOptions(this.phantomjs)
      .webDriverCapabilities(webDriverCapabilities)
      .webDriverClassName(webDriverClassName)
      .repositoryDetails(getRepositoryDetails())
      .build();
  }

  private RepositoryDetails getRepositoryDetails() {
    RepositoryDetails details = new RepositoryDetails();
    details.setRemoteRepositories(remoteRepositories);
    details.setRepositorySystem(repositorySystem);
    details.setRepositorySystemSession(repositorySystemSession);
    return details;
  }

  private boolean isSkipTests() {
    return this.skipTests || this.mvnTestSkip || this.skipJasmineTests;
  }
}
