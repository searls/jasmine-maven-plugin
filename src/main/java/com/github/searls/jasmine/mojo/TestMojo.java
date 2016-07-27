package com.github.searls.jasmine.mojo;

import com.github.klieber.phantomjs.locate.PhantomJsLocatorOptions;
import com.github.klieber.phantomjs.locate.RepositoryDetails;
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
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.jetty.server.Server;
import org.openqa.selenium.WebDriver;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Execute specs using Selenium Web Driver. Uses PhantomJsDriver for head-less execution by default.
 */
@Mojo(name = "test", defaultPhase = LifecyclePhase.TEST, requiresDependencyResolution = ResolutionScope.TEST)
public class TestMojo extends AbstractJasmineMojo {

  /**
   * Determines the Selenium WebDriver class we'll use to execute the tests. See the Selenium documentation for more details.
   * The plugin uses <a href="https://github.com/detro/ghostdriver">PhantomJSDriver</a> by default.
   * <p/>
   * <p>Some valid examples:</p>
   * <ul>
   * <li>org.openqa.selenium.htmlunit.HtmlUnitDriver</li>
   * <li>org.openqa.selenium.phantomjs.PhantomJSDriver</li>
   * <li>org.openqa.selenium.firefox.FirefoxDriver</li>
   * <li>org.openqa.selenium.ie.InternetExplorerDriver</li>
   * </ul>
   * <p></p>
   * See the webDriverCapabilities property for configuring driver specific properties.
   *
   * @since 1.1.0
   */
  @Parameter(defaultValue = "org.openqa.selenium.phantomjs.PhantomJSDriver")
  protected String webDriverClassName;

  /**
   * <p>Web driver capabilities used to initialize a DesiredCapabilities instance when creating a web driver.</p>
   * <p/>
   * <p>Capabilities value can be either a String, a List, or a Map.</p>
   * <p/>
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
  protected List<Capability> webDriverCapabilities = Collections.emptyList();

  /**
   * <p>Determines the browser and version profile that HtmlUnit will simulate. This setting does nothing if the plugin is configured not to use HtmlUnit.
   * This maps 1-to-1 with the public static instances found in {@link com.gargoylesoftware.htmlunit.BrowserVersion}.</p>
   * <p/>
   * <p>Some valid examples: CHROME, FIREFOX_17, INTERNET_EXPLORER_9, INTERNET_EXPLORER_10</p>
   *
   * @since 1.1.0
   * @deprecated Use the webDriverCapabilities parameter instead.
   */
  @Parameter(defaultValue = "FIREFOX_17")
  @Deprecated
  protected String browserVersion;

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
  protected String format;

  /**
   * <p>Configure which version of PhantomJS should be used and how it should be found. The core of the
   * <a href="http://klieber.github.io/phantomjs-maven-plugin"></a>phantomjs-maven-plugin</a> is used to provide this
   * functionality and this parameter should match the configuration of the
   * <a href="http://kylelieber.com/phantomjs-maven-plugin/install-mojo.html">phantomjs-maven-plugin install</a> goal.</p>
   * <p/>
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
  protected PhantomJsOptions phantomjs;

  /**
   * The name of the generated JUnit XML report.
   *
   * @since 1.1.0
   */
  @Parameter(defaultValue = "TEST-jasmine.xml")
  protected String junitXmlReportFileName;

  /**
   * Keep the server alive after the <code>jasmine:test</code> goal exists.
   * Useful if you need to run further analysis on your tests, like collecting code coverage.
   *
   * @since 1.3.1.0
   */
  @Parameter(property = "keepServerAlive", defaultValue = "false")
  protected boolean keepServerAlive;

  @Parameter(
    defaultValue = "${repositorySystemSession}",
    readonly = true
  )
  private RepositorySystemSession repositorySystemSession;

  @Parameter(
    defaultValue = "${project.remoteProjectRepositories}",
    readonly = true
  )
  private List<RemoteRepository> remoteRepositories;

  @Parameter(
    defaultValue = "${session}",
    readonly = true
  )
  private MavenSession mavenSession;

  private RepositorySystem repositorySystem;

  private final RelativizesFilePaths relativizesFilePaths;

  @Inject
  public TestMojo(RepositorySystem repositorySystem) {
    this.repositorySystem = repositorySystem;
    this.relativizesFilePaths = new RelativizesFilePaths();
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (!this.isSkipTests()) {
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
      JasmineResult result = this.executeSpecs(new URL(this.uriScheme + "://" + this.serverHostname + ":" + port));
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
      new File(this.jasmineTargetDir, this.junitXmlReportFileName),
      driver,
      this.timeout,
      this.debug,
      this.getLog(),
      this.format,
      getReporters()
    );
    return result;
  }

  private WebDriver createDriver() throws Exception {
    RepositoryDetails details = new RepositoryDetails();
    details.setRemoteRepositories(remoteRepositories);
    details.setRepositorySystem(repositorySystem);
    details.setRepositorySystemSession(repositorySystemSession);

    configure(mavenSession.getUserProperties());

    WebDriverFactory factory = new WebDriverFactory();
    factory.setWebDriverCapabilities(webDriverCapabilities);
    factory.setWebDriverClassName(webDriverClassName);
    factory.setDebug(debug);
    factory.setBrowserVersion(browserVersion);
    factory.setPhantomJsLocatorOptions(phantomjs);
    factory.setRepositoryDetails(details);

    return factory.createWebDriver();
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
