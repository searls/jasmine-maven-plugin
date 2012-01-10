package com.github.searls.jasmine;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.github.searls.jasmine.exception.StringifiesStackTraces;
import com.github.searls.jasmine.io.ScansDirectory;
import com.github.searls.jasmine.model.ScriptSearch;

public abstract class AbstractJasmineMojo extends AbstractMojo {

	/** Properties in order of most-to-least interesting for client projects to override **/
	
	/**
	 * @parameter default-value="${project.basedir}${file.separator}src${file.separator}main${file.separator}javascript" expression="${jsSrcDir}"
	 */
	private File jsSrcDir;
	
	/**
	 * @parameter default-value="${project.basedir}${file.separator}src${file.separator}test${file.separator}javascript" expression="${jsTestSrcDir}"
	 */
	private File jsTestSrcDir;

	/**
	 * Determines the Selenium WebDriver class we'll use to execute the tests. See the Selenium documentation for more details.
	 * The plugin uses HtmlUnit by default. 
	 * 	
	 * 	Some valid examples: org.openqa.selenium.htmlunit.HtmlUnitDriver, org.openqa.selenium.firefox.FirefoxDriver, org.openqa.selenium.ie.InternetExplorerDriver
	 * 
	 * @parameter default-value="org.openqa.selenium.htmlunit.HtmlUnitDriver"
	 */
	protected String webDriverClassName;
	
	/**
	 * Determines the browser and version profile that HtmlUnit will simulate. This setting does nothing if the plugin is configured not to use HtmlUnit.
	 * This maps 1-to-1 with the public static
	 * 	instances found in {@link com.gargoylesoftware.htmlunit.BrowserVersion}. 
	 * 	
	 * 	Some valid examples: FIREFOX_3_6, INTERNET_EXPLORER_6, INTERNET_EXPLORER_7, INTERNET_EXPLORER_8
	 * 
	 * @parameter default-value="FIREFOX_3"
	 */
	protected String browserVersion;
	
	/**
	 * Determines the format that jasmine:test will print to console. 
	 * 	Valid options:
	 * 		"documentation" - (default) - print specs in a nested format
	 *		"progress" - more terse, with a period for a passed specs and an 'F' for failures (e.g. '...F...')	
	 *
	 * @parameter default-value="documentation"
	 */
	protected String format;
	
	/**
	 * @parameter default-value="js" expression="${packageJavaScriptPath}"
	 */
	protected String packageJavaScriptPath;
	
	/**
	 * JavaScript sources (typically vendor/lib dependencies) that need to be loaded
	 * before other sources (and specs) in a particular order. Each source will first be
	 * searched for relative to ${jsSrcDir}, then ${jsTestSrcDir}, then (if it's not found in either)
	 * it will be included exactly as it appears in your POM.
	 * 
	 * Therefore, if jquery.js is in `${jsSrcDir}/vendor`, you would configure:
	 * 
	 *  	&lt;preloadSources&gt;
	 *			&lt;source&gt;vendor/z.js&lt;/source&gt;
	 *		&lt;/preloadSources&gt;
	 * 
	 * And z.js would load before all the other sources and specs.
	 * 
	 * @parameter
	 */
	protected List<String> preloadSources;
	
	/**
	 * It may be the case that the jasmine-maven-plugin doesn't currently suit all of your needs,
	 * 	and as a result the generated SpecRunner HTML files are set up in a way that you can't run
	 * 	your specs. Have no fear! Simply specify a custom spec runner template in the plugin configuration
	 * 	and make the changes you need. The default template is stored in `src/main/resources/jasmine-templates/SpecRunner.htmltemplate`,
	 * 	and the required template strings are tokenized in "$*$" patterns.
	 * 
	 * Example usage:
	 *  &lt;customRunnerTemplate&gt;${project.basedir}/src/test/resources/myCustomRunner.template&lt;/customRunnerTemplate&gt;
	 * 
	 * @parameter
	 */
	protected File customRunnerTemplate;

	/**
	 * Sometimes you want to have full control over how scriptloaders are configured. In order to interpolate custom configuration
	 * into the generated runnerTemplate, specify a file containing the additional config.
	 *
	 * 	 * Example usage:
	 *  &lt;customRunnerConfiguration&gt;${project.basedir}/src/test/resources/myCustomConfig.txt&lt;/customRunnerConfiguration&gt;
	 *
	 * @parameter
	 */
	protected File customRunnerConfiguration;
	
	/**
	 * @parameter default-value="${project.build.directory}${file.separator}jasmine"
	 */
	protected File jasmineTargetDir;
	
	
	/**
	 * @parameter expression="${skipTests}"
	 */
	protected boolean skipTests;
	
	/**
	 * @parameter default-value="true" expression="${haltOnFailure}"
	 */
	protected boolean haltOnFailure;
	
	/**
	 * Timeout for spec execution in seconds.
	 * 
	 * @parameter default-value=300
	 */
	protected int timeout;
	
	/**
	 * True to increase HtmlUnit output and attempt reporting on specs even if a timeout occurred.
	 * 
	 * @parameter default-value=false
	 */
	protected boolean debug;
	
	/**
	 * @parameter default-value="${project.build.directory}${file.separator}${project.build.finalName}"
	 */
	protected File packageDir;
	
	/**
	 * @parameter default-value="SpecRunner.html"
	 */
	protected String specRunnerHtmlFileName;
	
	/**
	 * @parameter default-value="ManualSpecRunner.html"
	 */
	protected String manualSpecRunnerHtmlFileName;
	
	/**
	 * @parameter default-value="TEST-jasmine.xml"
	 */
	protected String junitXmlReportFileName;
	
	/**
	 * @parameter default-value="spec"
	 */
	protected String specDirectoryName;
	
	/**
	 * @parameter default-value="src"
	 */
	protected String srcDirectoryName;

	/**
	 * @parameter default-value="${project.build.sourceEncoding}"
	 */
	protected String sourceEncoding;
	
	/**
	 * @parameter
	 */
	private List<String> sourceIncludes = ScansDirectory.DEFAULT_INCLUDES;
	
	/**
	 * @parameter
	 */
	private List<String> sourceExcludes = Collections.emptyList();
	
	/**
	 * @parameter
	 */
	private List<String> specIncludes = ScansDirectory.DEFAULT_INCLUDES;
	
	/**
	 * @parameter
	 */
	private List<String> specExcludes = Collections.emptyList();
	
	/**
	 * @parameter default-value="${project}"
	 */
	protected MavenProject mavenProject;
	
	/**
	 * @parameter default-value="8234" expression="${jasmine.serverPort}"
	 */
	protected int serverPort;

	/**
	 * Determines the strategy to use when generation the JasmineSpecRunner. This feature allows for custom
	 * implementation of the runner generator. Typically this is used when using different script runners.
	 *
	 *
	 * 	Some valid examples: REQUIRE_JS
	 *
	 * @parameter default-value="DEFAULT" expression="${jasmine.specRunnerTemplate}"
	 */
	protected String specRunnerTemplate;

    /**
     * Path to loader script, relative to jsSrcDir. Defaults to jsSrcDir/nameOfScript.js. Which script to look for is determined by
     * the selected spcRunnerTemplate. I.e require.js is used when REQUIRE_JS is selected as specRunnerTemplate.
     *
     * @parameter
     */
    protected String scriptLoaderPath;

	protected ScriptSearch sources;
	protected ScriptSearch specs;
	
	protected StringifiesStackTraces stringifiesStackTraces = new StringifiesStackTraces();

	public final void execute() throws MojoExecutionException, MojoFailureException {
		sources = new ScriptSearch(jsSrcDir,sourceIncludes,sourceExcludes);
		specs = new ScriptSearch(jsTestSrcDir,specIncludes,specExcludes);
		
		try {
			run();
		} catch(MojoFailureException e) {
			throw e;
		} catch(Exception e) {
			throw new MojoExecutionException("The jasmine-maven-plugin encountered an exception: \n"+stringifiesStackTraces.stringify(e),e);
		}
	}
	
	public abstract void run() throws Exception;

	public String getSourceEncoding() {
		return sourceEncoding;
	}

	public File getCustomRunnerTemplate() {
		return customRunnerTemplate;
	}

	public String getSpecRunnerTemplate() {
		return specRunnerTemplate;
	}

	public File getJasmineTargetDir() {
		return jasmineTargetDir;
	}

	public String getSrcDirectoryName() {
		return srcDirectoryName;
	}

	public ScriptSearch getSources() {
		return sources;
	}

	public ScriptSearch getSpecs() {
		return specs;
	}

	public String getSpecDirectoryName() {
		return specDirectoryName;
	}

	public List<String> getPreloadSources() {
		return preloadSources;
	}

	public MavenProject getMavenProject() {
		return mavenProject;
	}

	public File getCustomRunnerConfiguration() {
		return customRunnerConfiguration;

	}

    public String getScriptLoaderPath() {
        return scriptLoaderPath;
    }
}
