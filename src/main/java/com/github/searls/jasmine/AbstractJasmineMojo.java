package com.github.searls.jasmine;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

public abstract class AbstractJasmineMojo extends AbstractMojo {

	/** Properties in order of most-to-least interesting for client projects to override **/
	
	/**
	 * @parameter default-value="${project.basedir}${file.separator}src${file.separator}main${file.separator}javascript" expression="${jsSrcDir}"
	 */
	protected File jsSrcDir;
	
	/**
	 * @parameter default-value="${project.basedir}${file.separator}src${file.separator}test${file.separator}javascript" expression="${jsTestSrcDir}"
	 */
	protected File jsTestSrcDir;
	
	/**
	 * Determines the browser and version profile to execute the headless specs against. Because the plugin
	 * 	executes specs using HtmlUnit, this maps 1-to-1 with the public static
	 * 	instances found in {@link com.gargoylesoftware.htmlunit.BrowserVersion}.
	 * 	
	 * 	Some valid examples: FIREFOX_3_6, INTERNET_EXPLORER_6, INTERNET_EXPLORER_7, INTERNET_EXPLORER_8
	 * 
	 * @parameter default-value="FIREFOX_3"
	 */
	protected String browserVersion;
	
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
	 * @parameter default-value="${project}"
	 */
	protected MavenProject mavenProject;
}
