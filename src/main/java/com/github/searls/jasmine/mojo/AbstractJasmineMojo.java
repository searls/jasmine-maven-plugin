package com.github.searls.jasmine.mojo;

import com.github.searls.jasmine.config.ImmutableJasmineConfiguration;
import com.github.searls.jasmine.config.ImmutableServerConfiguration;
import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.config.ServerConfiguration;
import com.github.searls.jasmine.io.ScansDirectory;
import com.github.searls.jasmine.model.FileSystemReporter;
import com.github.searls.jasmine.model.ImmutableScriptSearch;
import com.github.searls.jasmine.model.Reporter;
import com.github.searls.jasmine.model.ScriptSearch;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.runner.SpecRunnerTemplate;
import com.github.searls.jasmine.thirdpartylibs.ProjectClassLoaderFactory;
import com.google.common.base.Optional;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractJasmineMojo extends AbstractMojo {

  protected static final String CUSTOM_RUNNER_CONFIGURATION_PARAM = "customRunnerConfiguration";
  protected static final String CUSTOM_RUNNER_TEMPLATE_PARAM = "customRunnerTemplate";

  // Properties in order of most-to-least interesting for client projects to override

  /**
   * Directory storing your JavaScript.
   *
   * @since 1.1.0
   */
  @Parameter(
    property = "jsSrcDir",
    defaultValue = "${project.basedir}${file.separator}src${file.separator}main${file.separator}javascript")
  private File jsSrcDir = null;

  /**
   * Directory storing your Jasmine Specs.
   *
   * @since 1.1.0
   */
  @Parameter(
    property = "jsTestSrcDir",
    defaultValue = "${project.basedir}${file.separator}src${file.separator}test${file.separator}javascript")
  private File jsTestSrcDir = null;

  /**
   * <p>JavaScript sources (typically vendor/lib dependencies) that need to be loaded
   * before other sources (and specs) in a particular order. Each source will first be
   * searched for relative to <code>${jsSrcDir}</code>, then <code>${jsTestSrcDir}</code>,
   * then (if it's not found in either) it will be included exactly as it appears in your POM.</p>
   * <br>
   * <p>Therefore, if jquery.js is in <code>${jsSrcDir}/vendor</code>, you would configure:</p>
   * <pre>
   * &lt;preloadSources&gt;
   *   &lt;source&gt;vendor/jquery.js&lt;/source&gt;
   * &lt;/preloadSources&gt;
   * </pre>
   * <br>
   * <p>And jquery.js would load before all the other sources and specs.</p>
   *
   * @since 1.1.0
   */
  @Parameter
  private List<String> preloadSources = Collections.emptyList();

  /**
   * <p>It may be the case that the jasmine-maven-plugin doesn't currently suit all of your needs,
   * and as a result the generated SpecRunner HTML files are set up in a way that you can't run
   * your specs. Have no fear! Simply specify a custom spec runner template in the plugin configuration
   * and make the changes you need.</p>
   * <br>
   * <p>Potential values are a filesystem path, a URL, or a classpath resource. The default template is
   * stored in <code>src/main/resources/jasmine-templates/SpecRunner.htmltemplate</code>, and the
   * required template strings are tokenized in "$*$" patterns.</p>
   * <br>
   * <p>Example usage:</p>
   * <pre>
   * &lt;customRunnerTemplate&gt;${project.basedir}/src/test/resources/myCustomRunner.template&lt;/customRunnerTemplate&gt;
   * </pre>
   *
   * @since 1.1.0
   */
  @Parameter
  private String customRunnerTemplate = null;

  /**
   * <p>Sometimes you want to have full control over how scriptloaders are configured. In order to
   * interpolate custom configuration into the generated runnerTemplate, specify a file containing
   * the additional config. Potential values are a filesystem path, a URL, or a classpath resource.</p>
   * <br>
   * <p>Example usage:</p>
   * <pre>
   * &lt;customRunnerConfiguration&gt;${project.basedir}/src/test/resources/myCustomConfig.txt&lt;/customRunnerConfiguration&gt;
   * </pre>
   *
   * @since 1.1.0
   */
  @Parameter
  private String customRunnerConfiguration = null;

  /**
   * <p> Specify a custom reporter to be used to print the test report.</p>
   * <p>Example usage:</p>
   * <pre>
   * &lt;reporters&gt;
   *   &lt;reporter&gt;
   *     &lt;reporterName&gt;${project.basedir}/src/test/resources/myCustomReporter.js&lt;/reporterName&gt;
   *   &lt;/reporter&gt;
   *   &lt;reporter&gt;
   *     &lt;reporterName&gt;STANDARD&lt;/reporterName&gt;
   *   &lt;/reporter&gt;
   * &lt;/reporters&gt;
   * </pre>
   */
  @Parameter
  private List<Reporter> reporters = new ArrayList<Reporter>();

  /**
   * <p> Specify a custom file system reporter to be used to store the test report.</p>
   * <p>Example usage:</p>
   * <pre>
   * &lt;fileSystemReporters&gt;
   *   &lt;reporter&gt;
   *     &lt;fileName&gt;MyFile.log&lt;/fileName&gt;
   *     &lt;reporterName&gt;${project.basedir}/src/test/resources/myCustomReporter.js&lt;/reporterName&gt;
   *   &lt;/reporter&gt;
   *   &lt;reporter&gt;
   *     &lt;fileName&gt;Test-jasmine.xml&lt;/fileName&gt;
   *     &lt;reporterName&gt;JUNIT_XML&lt;/reporterName&gt;
   *   &lt;/reporter&gt;
   * &lt;/fileSystemReporters&gt;
   * </pre>
   */
  @Parameter
  private List<FileSystemReporter> fileSystemReporters = new ArrayList<FileSystemReporter>();

  /**
   * Target directory for files created by the plugin.
   *
   * @since 1.1.0
   */
  @Parameter(defaultValue = "${project.build.directory}${file.separator}jasmine")
  private File jasmineTargetDir = null;

  /**
   * The name of the Spec Runner file.
   *
   * @since 1.1.0
   */
  @Parameter(defaultValue = "SpecRunner.html")
  private String specRunnerHtmlFileName = "SpecRunner.html";

  /**
   * The name of the directory the specs will be deployed to on the server.
   *
   * @since 1.1.0
   */
  @Parameter(defaultValue = "spec")
  private String specDirectoryName = "spec";

  /**
   * The name of the directory the sources will be deployed to on the server.
   *
   * @since 1.1.0
   */
  @Parameter(defaultValue = "src")
  private String srcDirectoryName = "src";

  /**
   * The source encoding.
   *
   * @since 1.1.0
   */
  @Parameter(defaultValue = "${project.build.sourceEncoding}")
  private String sourceEncoding = StandardCharsets.UTF_8.name();

  /**
   * <p>Allows specifying which source files should be included and in what order.</p>
   * <pre>
   * &lt;sourceIncludes&gt;
   *   &lt;include&gt;vendor/&#42;&#42;/&#42;.js&lt;/include&gt;
   *   &lt;include&gt;myBootstrapFile.js&lt;/include&gt;
   *   &lt;include&gt;&#42;&#42;/&#42;.js&lt;/include&gt;
   * &lt;/sourceIncludes&gt;
   * </pre>
   * <br>
   * <p>Default <code>sourceIncludes</code>:</p>
   * <pre>
   * &lt;sourceIncludes&gt;
   *   &lt;include&gt;&#42;&#42;/&#42;.js&lt;/include&gt;
   * &lt;/sourceIncludes&gt;
   * </pre>
   *
   * @since 1.1.0
   */
  @Parameter
  private final List<String> sourceIncludes = ScansDirectory.DEFAULT_INCLUDES;

  /**
   * <p>Just like <code>sourceIncludes</code>, but will exclude anything matching the provided patterns.</p>
   * <p>There are no <code>sourceExcludes</code> by default.</p>
   *
   * @since 1.1.0
   */
  @Parameter
  private final List<String> sourceExcludes = Collections.emptyList();

  /**
   * <p>I often find myself needing control of the spec include order
   * when I have some global spec helpers or spec-scoped dependencies, like:</p>
   * <pre>
   * &lt;specIncludes&gt;
   *   &lt;include&gt;jasmine-jquery.js&lt;/include&gt;
   *   &lt;include&gt;spec-helper.js&lt;/include&gt;
   *   &lt;include&gt;&#42;&#42;/&#42;.js&lt;/include&gt;
   * &lt;/specIncludes&gt;
   * </pre>
   * <br>
   * <p>Default <code>specIncludes</code>:</p>
   * <pre>
   * &lt;specIncludes&gt;
   *   &lt;include&gt;&#42;&#42;/&#42;.js&lt;/include&gt;
   * &lt;/specIncludes&gt;
   * </pre>
   *
   * @since 1.1.0
   */
  @Parameter
  private final List<String> specIncludes = ScansDirectory.DEFAULT_INCLUDES;

  /**
   * <p>Just like <code>specIncludes</code>, but will exclude anything matching the provided patterns.</p>
   * <p>There are no <code>specExcludes</code> by default.</p>
   *
   * @since 1.1.0
   */
  @Parameter
  private final List<String> specExcludes = Collections.emptyList();

  /**
   * <p>Used by the <code>jasmine:bdd</code> goal to specify port to run the server under.</p>
   * <br>
   * <p>The <code>jasmine:test</code> goal always uses a random available port so this property is ignored.</p>
   *
   * @since 1.1.0
   */
  @Parameter(property = "jasmine.serverPort", defaultValue = "8234")
  private int serverPort = 8234;

  /**
   * <p>Specify the URI scheme in which to access the SpecRunner.</p>
   *
   * @since 1.3.1.4
   */
  @Parameter(property = "jasmine.uriScheme", defaultValue = "http")
  private String uriScheme = "http";

  /**
   * <p>Not used by the <code>jasmine:bdd</code> goal.</p>
   * <br>
   * <p>The <code>jasmine:test</code> goal to specify hostname where the server is running.  Useful when using
   * the RemoteWebDriver.</p>
   *
   * @since 1.3.1.4
   */
  @Parameter(property = "jasmine.serverHostname", defaultValue = "localhost")
  private String serverHostname = "localhost";

  /**
   * <p>Determines the strategy to use when generation the JasmineSpecRunner. This feature allows for custom
   * implementation of the runner generator. Typically this is used when using different script runners.</p>
   * <br>
   * <p>Some valid examples: DEFAULT, REQUIRE_JS</p>
   *
   * @since 1.1.0
   */
  @Parameter(property = "jasmine.specRunnerTemplate", defaultValue = "DEFAULT")
  private SpecRunnerTemplate specRunnerTemplate = SpecRunnerTemplate.DEFAULT;

  /**
   * <p>Automatically refresh the test runner at the given interval (specified in seconds) when using the <code>jasmine:bdd</code> goal.</p>
   * <p>A value of <code>0</code> disables the automatic refresh (which is the default).</p>
   *
   * @since 1.3.1.1
   */
  @Parameter(property = "jasmine.autoRefreshInterval", defaultValue = "0")
  private int autoRefreshInterval = 0;

  /**
   * <p>Specify additional contexts to make available.</p>
   * <pre>
   * &lt;additionalContexts&gt;
   *   &lt;context&gt;
   *     &lt;contextRoot&gt;lib&lt;/contextRoot&gt;
   *     &lt;directory&gt;${project.basedir}/src/main/lib&lt;/directory&gt;
   *   &lt;/context&gt;
   *   &lt;context&gt;
   *     &lt;contextRoot&gt;test/lib&lt;/contextRoot&gt;
   *     &lt;directory&gt;${project.basedir}/src/test/lib&lt;/directory&gt;
   *   &lt;/context&gt;
   * &lt;/additionalContexts&gt;
   * </pre>
   *
   * @since 1.3.1.5
   */
  @Parameter
  private List<Context> additionalContexts = Collections.emptyList();

  private final MavenProject mavenProject;
  private final ReporterType reporterType;
  private final ResourceRetriever resourceRetriever;
  private final ReporterRetriever reporterRetriever;

  AbstractJasmineMojo(MavenProject mavenProject,
                      ReporterType reporterType,
                      ResourceRetriever resourceRetriever,
                      ReporterRetriever reporterRetriever) {
    this.mavenProject = mavenProject;
    this.reporterType = reporterType;
    this.resourceRetriever = resourceRetriever;
    this.reporterRetriever = reporterRetriever;
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      this.run(getServerConfiguration(), getJasmineConfiguration());
    } catch (MojoFailureException e) {
      throw e;
    } catch (Exception e) {
      throw new MojoExecutionException("The jasmine-maven-plugin encountered an exception:", e);
    }
  }

  protected final MavenProject getMavenProject() {
    return mavenProject;
  }

  protected abstract void run(ServerConfiguration serverConfiguration,
                              JasmineConfiguration jasmineConfiguration) throws Exception;


  private ServerConfiguration getServerConfiguration() {
    return ImmutableServerConfiguration.builder()
      .uriScheme(this.uriScheme)
      .serverHostname(this.serverHostname)
      .serverPort(this.serverPort)
      .build();
  }

  private JasmineConfiguration getJasmineConfiguration() throws MojoExecutionException {
    return ImmutableJasmineConfiguration.builder()
      .customRunnerConfiguration(getCustomRunnerConfigurationFile())
      .customRunnerTemplate(getCustomRunnerTemplateFile())
      .contexts(getContexts())
      .projectClassLoader(getProjectClassLoader())
      .autoRefreshInterval(this.autoRefreshInterval)
      .preloadSources(this.preloadSources)
      .basedir(this.mavenProject.getBasedir())
      .srcDirectoryName(this.srcDirectoryName)
      .sources(this.getSources())
      .specDirectoryName(this.specDirectoryName)
      .specs(this.getSpecs())
      .specRunnerHtmlFileName(this.specRunnerHtmlFileName)
      .specRunnerTemplate(this.specRunnerTemplate)
      .reporters(getReporters())
      .fileSystemReporters(getFileSystemReporters())
      .reporterType(this.reporterType)
      .jasmineTargetDir(this.jasmineTargetDir)
      .sourceEncoding(this.sourceEncoding)
      .build();
  }

  private List<FileSystemReporter> getFileSystemReporters() throws MojoExecutionException {
    return reporterRetriever.retrieveFileSystemReporters(
      this.fileSystemReporters,
      this.jasmineTargetDir,
      this.mavenProject
    );
  }

  private Optional<File> getCustomRunnerTemplateFile() throws MojoExecutionException {
    return resourceRetriever.getResourceAsFile(
      CUSTOM_RUNNER_TEMPLATE_PARAM,
      this.customRunnerTemplate,
      this.mavenProject
    );
  }

  private Optional<File> getCustomRunnerConfigurationFile() throws MojoExecutionException {
    return resourceRetriever.getResourceAsFile(
      CUSTOM_RUNNER_CONFIGURATION_PARAM,
      this.customRunnerConfiguration,
      this.mavenProject
    );
  }

  private List<Reporter> getReporters() throws MojoExecutionException {
    return reporterRetriever.retrieveReporters(this.reporters, this.mavenProject);
  }

  private ClassLoader getProjectClassLoader() {
    return new ProjectClassLoaderFactory(mavenProject.getArtifacts()).create();
  }

  private List<Context> getContexts() {
    List<Context> contexts = new ArrayList<Context>();
    contexts.add(new Context(this.srcDirectoryName, this.jsSrcDir));
    contexts.add(new Context(this.specDirectoryName, this.jsTestSrcDir));
    contexts.addAll(additionalContexts);
    return contexts;
  }

  private ScriptSearch getSpecs() {
    return ImmutableScriptSearch.builder()
      .directory(this.jsTestSrcDir)
      .includes(this.specIncludes)
      .excludes(this.specExcludes)
      .build();
  }


  private ScriptSearch getSources() {
    return ImmutableScriptSearch.builder()
      .directory(this.jsSrcDir)
      .includes(this.sourceIncludes)
      .excludes(this.sourceExcludes)
      .build();
  }
}
