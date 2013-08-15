package com.github.searls.jasmine.mojo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.resource.ResourceManager;
import org.codehaus.plexus.resource.loader.FileResourceLoader;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.exception.StringifiesStackTraces;
import com.github.searls.jasmine.io.ScansDirectory;
import com.github.searls.jasmine.model.ScriptSearch;
import com.github.searls.jasmine.runner.SpecRunnerTemplate;

public abstract class AbstractJasmineMojo extends AbstractMojo implements JasmineConfiguration {

	private static final String ERROR_FILE_DNE = "Invalid value for parameter '%s'. File does not exist: %s";

	// Properties in order of most-to-least interesting for client projects to override

	/**
	 * Directory storing your JavaScript.
	 * @since 1.1.0
	 */
	@Parameter(
			property="jsSrcDir",
			defaultValue="${project.basedir}${file.separator}src${file.separator}main${file.separator}javascript")
	private File jsSrcDir;

	/**
	 * Directory storying your Jasmine Specs.
	 * @since 1.1.0
	 */
	@Parameter(
			property="jsTestSrcDir",
			defaultValue="${project.basedir}${file.separator}src${file.separator}test${file.separator}javascript")
	private File jsTestSrcDir;

	/**
	 * Determines the Selenium WebDriver class we'll use to execute the tests. See the Selenium documentation for more details.
	 * The plugin uses <a href="http://htmlunit.sourceforge.net/">HtmlUnit</a> by default.
	 *
	 * <p>Some valid examples:</p>
	 * <ul>
	 *   <li>org.openqa.selenium.htmlunit.HtmlUnitDriver</li>
	 *   <li>org.openqa.selenium.phantomjs.PhantomJSDriver</li>
	 *   <li>org.openqa.selenium.firefox.FirefoxDriver</li>
	 *   <li>org.openqa.selenium.ie.InternetExplorerDriver</li>
	 * </ul>
	 * <p/>
	 * For org.openqa.selenium.phantomjs.PhantomJSDriver, see the webDriverCapabilities property.
	 *
	 * @since 1.1.0
	 */
	@Parameter(defaultValue="org.openqa.selenium.htmlunit.HtmlUnitDriver")
	protected String webDriverClassName;

	/**
	 * Web driver capabilities used to initialize a DesiredCapabilities instance when creating a web driver.
	 * <p/>
	 * This property will be ignored if org.openqa.selenium.htmlunit.HtmlUnitDriver is used; use the browserVersion
	 * property instead.
	 * <p/>
	 * For org.openqa.selenium.phantomjs.PhantomJSDriver, include "phantomjs.binary.path" if phantomJS is not in the
	 * system command path of the build machine.
	 *
	 * @since 1.3.1.1
	 */
	@Parameter
	protected Map<String, String> webDriverCapabilities;

	/**
	 * <p>Determines the browser and version profile that HtmlUnit will simulate. This setting does nothing if the plugin is configured not to use HtmlUnit.
	 * This maps 1-to-1 with the public static instances found in {@link com.gargoylesoftware.htmlunit.BrowserVersion}.</p>
	 *
	 * <p>Some valid examples: CHROME, FIREFOX_3_6, INTERNET_EXPLORER_7, INTERNET_EXPLORER_8, INTERNET_EXPLORER_9</p>
	 *
	 * @since 1.1.0
	 */
	@Parameter(defaultValue="FIREFOX_3_6")
	protected String browserVersion;

	/**
	 * <p>Determines the format that jasmine:test will print to console.</p>
	 * <p>Valid options:</p>
	 * <ul>
	 *   <li>"documentation" - (default) - print specs in a nested format</li>
	 *   <li>"progress" - more terse, with a period for a passed specs and an 'F' for failures (e.g. '...F...')</li>
	 * </ul>
	 *
	 * @since 1.1.0
	 */
	@Parameter(defaultValue="documentation")
	protected String format;

	/**
	 * <p>JavaScript sources (typically vendor/lib dependencies) that need to be loaded
	 * before other sources (and specs) in a particular order. Each source will first be
	 * searched for relative to <code>${jsSrcDir}</code>, then <code>${jsTestSrcDir}</code>,
	 * then (if it's not found in either) it will be included exactly as it appears in your POM.</p>
	 *
	 * <p>Therefore, if jquery.js is in <code>${jsSrcDir}/vendor</code>, you would configure:</p>
	 * <pre>
	 * &lt;preloadSources&gt;
	 *   &lt;source&gt;vendor/jquery.js&lt;/source&gt;
	 * &lt;/preloadSources&gt;
	 * </pre>
	 * 
	 * <p>And jquery.js would load before all the other sources and specs.</p>
	 * 
	 * @since 1.1.0
	 */
	@Parameter
	protected List<String> preloadSources;

	/**
	 * <p>It may be the case that the jasmine-maven-plugin doesn't currently suit all of your needs,
	 * and as a result the generated SpecRunner HTML files are set up in a way that you can't run
	 * your specs. Have no fear! Simply specify a custom spec runner template in the plugin configuration
	 * and make the changes you need.</p>
	 * 
	 * <p>Potential values are a filesystem path, a URL, or a classpath resource. The default template is
	 * stored in <code>src/main/resources/jasmine-templates/SpecRunner.htmltemplate</code>, and the
	 * required template strings are tokenized in "$*$" patterns.</p>
	 *
	 * <p>Example usage:</p>
	 * <pre>
	 * &lt;customRunnerTemplate&gt;${project.basedir}/src/test/resources/myCustomRunner.template&lt;/customRunnerTemplate&gt;
	 * </pre>
	 *
	 * @since 1.1.0
	 */
	@Parameter
	protected String customRunnerTemplate;

	/**
	 * <p>Sometimes you want to have full control over how scriptloaders are configured. In order to
	 * interpolate custom configuration into the generated runnerTemplate, specify a file containing
	 * the additional config. Potential values are a filesystem path, a URL, or a classpath resource.</p>
	 *
	 * <p>Example usage:</p>
	 * <pre>
	 * &lt;customRunnerConfiguration&gt;${project.basedir}/src/test/resources/myCustomConfig.txt&lt;/customRunnerConfiguration&gt;
	 * </pre>
	 *
	 * @since 1.1.0
	 */
	@Parameter
	protected String customRunnerConfiguration;

	/**
	 * Target directory for files created by the plugin.
	 * 
	 * @since 1.1.0
	 */
	@Parameter(defaultValue="${project.build.directory}${file.separator}jasmine")
	protected File jasmineTargetDir;

	/**
	 * Skip execution of tests.
	 * 
	 * @since 1.1.0
	 * @see <a href="http://maven.apache.org/general.html#skip-test">http://maven.apache.org/general.html#skip-test</a>
	 */
	@Parameter(property="skipTests")
	protected boolean skipTests;
	
	/**
   * Skip compilation and execution of tests.
   * 
   * @since 1.3.1.3
   * @see <a href="http://maven.apache.org/general.html#skip-test">http://maven.apache.org/general.html#skip-test</a>
   */
	@Parameter(property="maven.test.skip")
  protected boolean mvnTestSkip;

	/**
	 * Halt the build on test failure.
	 * 
	 * @since 1.1.0
	 */
	@Parameter(property="haltOnFailure", defaultValue="true")
	protected boolean haltOnFailure;

	/**
	 * Timeout for spec execution in seconds.
	 * 
	 * @since 1.1.0
	 */
	@Parameter(defaultValue="300")
	protected int timeout;

	/**
	 * True to increase HtmlUnit output and attempt reporting on specs even if a timeout occurred.
	 *
	 * @since 1.1.0
	 */
	@Parameter(defaultValue="false")
	protected boolean debug;

	/**
	 * The name of the Spec Runner file.
	 *
	 * @since 1.1.0
	 */
	@Parameter(defaultValue="SpecRunner.html")
	protected String specRunnerHtmlFileName;

	/**
	 * The name of the Manual Spec Runner.
	 *
	 * @since 1.1.0
	 */
	@Parameter(defaultValue="ManualSpecRunner.html")
	protected String manualSpecRunnerHtmlFileName;

	/**
	 * The name of the generated JUnit XML report.
	 * 
	 * @since 1.1.0
	 */
	@Parameter(defaultValue="TEST-jasmine.xml")
	protected String junitXmlReportFileName;

	/**
	 * The name of the directory the specs will be deployed to on the server.
	 * 
	 * @since 1.1.0
	 */
	@Parameter(defaultValue="spec")
	protected String specDirectoryName;

	/**
	 * The name of the directory the sources will be deployed to on the server.
	 * 
	 * @since 1.1.0
	 */
	@Parameter(defaultValue="src")
	protected String srcDirectoryName;

	/**
	 * The source encoding.
	 * 
	 * @since 1.1.0
	 */
	@Parameter(defaultValue="${project.build.sourceEncoding}")
	protected String sourceEncoding;

	/**
	 * Keep the server alive after the <code>jasmine:test</code> goal exists.
	 * Useful if you need to run further analysis on your tests, like collecting code coverage.
	 * 
	 * @since 1.3.1.0
	 */
	@Parameter(property="keepServerAlive", defaultValue="false")
	protected boolean keepServerAlive;

	/**
	 * &lt;sourceIncludes&gt;
	 *   &lt;include&gt;vendor/&#42;&#42;/&#42;.js&lt;/include&gt;
	 *   &lt;include&gt;myBootstrapFile.js&lt;/include&gt;
	 *   &lt;include&gt;&#42;&#42;/&#42;.js&lt;/include&gt;
	 *   &lt;include&gt;&#42;&#42;/&#42;.coffee&lt;/include&gt;
	 * &lt;/sourceIncludes&gt;
	 * </pre>
	 * 
	 * <p>Default <code>sourceIncludes</code>:</p>
	 * <pre>
	 * &lt;sourceIncludes&gt;
	 *   &lt;include&gt;&#42;&#42;/&#42;.js&lt;/include&gt;
	 *   &lt;include&gt;&#42;&#42;/&#42;.coffee&lt;/include&gt;
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
	 *   &lt;include&gt;&#42;&#42;/&#42;.coffee&lt;/include&gt;
	 * &lt;/specIncludes&gt;
	 * </pre>
	 * 
	 * <p>Default <code>specIncludes</code>:</p>
	 * <pre>
	 * &lt;specIncludes&gt;
	 *   &lt;include&gt;&#42;&#42;/&#42;.js&lt;/include&gt;
	 *   &lt;include&gt;&#42;&#42;/&#42;.coffee&lt;/include&gt;
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
	 * 
	 * <p>The <code>jasmine:test</code> goal always uses a random available port so this property is ignored.</p>
	 * 
	 * @since 1.1.0
	 */
	@Parameter(property="jasmine.serverPort", defaultValue="8234")
	protected int serverPort;

	/**
	 * <p>Determines the strategy to use when generation the JasmineSpecRunner. This feature allows for custom
	 * implementation of the runner generator. Typically this is used when using different script runners.</p>
	 *
	 * <p>Some valid examples: DEFAULT, REQUIRE_JS</p>
	 * 
	 * @since 1.1.0
	 */
	@Parameter(property="jasmine.specRunnerTemplate", defaultValue="DEFAULT")
	protected SpecRunnerTemplate specRunnerTemplate;

	/**
	 * <p>Path to loader script, relative to jsSrcDir. Defaults to jsSrcDir/nameOfScript.js. Which script to look for is determined by
	 * the selected spcRunnerTemplate. I.e require.js is used when REQUIRE_JS is selected as specRunnerTemplate.</p>
	 *
	 * @since 1.1.0
	 * @deprecated Specify script loader path using the <code>preloadSources</code> parameter instead.
	 */
	@Parameter
	@Deprecated
	protected String scriptLoaderPath;

	/**
	 * <p>Automatically refresh the test runner at the given interval (specified in seconds) when using the <code>jasmine:bdd</code> goal.</p>
	 * <p>A value of <code>0</code> disables the automatic refresh (which is the default).</p>
	 * 
	 * @since 1.3.1.1
	 */
	@Parameter(property="jasmine.autoRefreshInterval", defaultValue="0")
	protected int autoRefreshInterval;

	@Parameter(defaultValue="${project}", readonly=true)
	protected MavenProject mavenProject;

	@Component
	protected ResourceManager locator;

	protected ScriptSearch sources;
	protected ScriptSearch specs;

	protected StringifiesStackTraces stringifiesStackTraces = new StringifiesStackTraces();

	private File customRunnerTemplateFile;
	private File customRunnerConfigurationFile;

	@Override
	public final void execute() throws MojoExecutionException, MojoFailureException {
		this.loadResources();

		this.sources = new ScriptSearch(this.jsSrcDir,this.sourceIncludes,this.sourceExcludes);
		this.specs = new ScriptSearch(this.jsTestSrcDir,this.specIncludes,this.specExcludes);

		try {
			this.run();
		} catch(MojoFailureException e) {
			throw e;
		} catch(Exception e) {
			throw new MojoExecutionException("The jasmine-maven-plugin encountered an exception: \n"+this.stringifiesStackTraces.stringify(e),e);
		}
	}

	public abstract void run() throws Exception;

	@Override
	public String getSourceEncoding() {
		return this.sourceEncoding;
	}

	@Override
	public File getCustomRunnerTemplate() {
		return this.customRunnerTemplateFile;
	}

	@Override
	public SpecRunnerTemplate getSpecRunnerTemplate() {
		return this.specRunnerTemplate;
	}

	@Override
	public File getJasmineTargetDir() {
		return this.jasmineTargetDir;
	}

	@Override
	public String getSrcDirectoryName() {
		return this.srcDirectoryName;
	}

	@Override
	public ScriptSearch getSources() {
		return this.sources;
	}

	@Override
	public ScriptSearch getSpecs() {
		return this.specs;
	}

	@Override
	public String getSpecDirectoryName() {
		return this.specDirectoryName;
	}

	@Override
	public List<String> getPreloadSources() {
		this.addRequireJsIfNecessary(); // This is temporary until the scriptLoaderPath parameter is removed
		return this.preloadSources;
	}

	private void addRequireJsIfNecessary() {
		String scriptLoaderPath = this.getScriptLoaderPath() == null ? "require.js" : this.getScriptLoaderPath();
		String requireJsPath = String.format("%s/%s", this.jsSrcDir, scriptLoaderPath);
		if (SpecRunnerTemplate.REQUIRE_JS.equals(this.specRunnerTemplate)) {
			File requireJsFile = new File(requireJsPath);
			if (requireJsFile.exists()) {
				if (this.preloadSources == null) {
					this.preloadSources = new ArrayList<String>();
				}
				this.preloadSources.add(requireJsPath);
			}
		}
	}

	@Override
	public int getAutoRefreshInterval() {
		return this.autoRefreshInterval;
	}

	public MavenProject getMavenProject() {
		return this.mavenProject;
	}

	@Override
	public File getCustomRunnerConfiguration() {
		return this.customRunnerConfigurationFile;
	}

	@Deprecated
	@Override
	public String getScriptLoaderPath() {
		return this.scriptLoaderPath;
	}

	@Override
	public File getBasedir() {
		return this.mavenProject.getBasedir();
	}

	private void loadResources() throws MojoExecutionException {
		this.customRunnerTemplateFile = this.getResourceAsFile("customRunnerTemplate", this.customRunnerTemplate);
		this.customRunnerConfigurationFile = this.getResourceAsFile("customRunnerConfiguration", this.customRunnerConfiguration);
	}

	private File getResourceAsFile(String parameter, String resourceLocation) throws MojoExecutionException {
		File file = null;

		if (resourceLocation != null) {
			this.locator.addSearchPath( "url", "" );
			this.locator.addSearchPath( FileResourceLoader.ID, this.mavenProject.getFile().getParentFile().getAbsolutePath() );

			ClassLoader origLoader = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
				try {
					file = this.locator.getResourceAsFile(resourceLocation);
				} catch (Exception e) {
					throw new MojoExecutionException(String.format(ERROR_FILE_DNE,parameter,resourceLocation));
				}
			}
			finally {
				Thread.currentThread().setContextClassLoader( origLoader );
			}
		}
		return file;
	}
}
