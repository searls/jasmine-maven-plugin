package searls.jasmine;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

public abstract class AbstractJasmineMojo extends AbstractMojo {

	/** Properties in order of most-to-least interesting for client projects to override **/
	
	/**
	 * @parameter expression="${jsSrcDir}" default-value="${project.basedir}/src/main/javascript"
	 */
	protected String jsSrcDir;
	
	/**
	 * @parameter expression="${jsTestSrcDir}" default-value="${project.basedir}/src/test/javascript"
	 */
	protected String jsTestSrcDir;
	
	/**
	 * @parameter expression="${packageJavaScriptPath}" default-value="js"
	 */
	protected String packageJavaScriptPath;
	
	/**
	 * JavaScript sources (typically vendor/lib dependencies) that need to be loaded
	 * before other sources (and specs) in a particular order, these are relative to the ${jsSrcDir} 
	 * directory! Therefore, if jquery.js is in `${jsSrcDir}/vendor`, you would configure:
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
	 * @parameter default-value="${project.build.directory}/jasmine"
	 */
	protected String jasmineTargetDir;
	
	
	/**
	 * @parameter expression="${skipTests}"
	 */
	protected boolean skipTests;
	
	/**
	 * @parameter expression="${haltOnFailure}" default-value="true"
	 */
	protected boolean haltOnFailure;
	
	
	/**
	 * @parameter default-value="${project.build.directory}/${project.build.finalName}"
	 */
	protected String packageDir;
	
	/**
	 * @parameter default-value="SpecRunner.html"
	 */
	protected String specRunnerHtmlFileName;
	
	/**
	 * @parameter default-value="spec"
	 */
	protected String specDirectoryName;
	
	/**
	 * @parameter default-value="src"
	 */
	protected String srcDirectoryName;

	/**
	 * @parameter default-value="${project}"
	 */
	protected MavenProject mavenProject;

	/**
	 * @parameter default-value="${plugin.artifacts}"
	 */
	protected List<Artifact> pluginArtifacts;
}
