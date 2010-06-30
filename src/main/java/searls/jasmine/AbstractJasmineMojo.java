package searls.jasmine;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

public abstract class AbstractJasmineMojo extends AbstractMojo {

	/** Properties in order of most-to-least interesting for client projects to override **/
	
	/**
	 * @parameter default-value="${project.basedir}/src/main/javascript" expression="${jsSrcDir}"
	 */
	protected String jsSrcDir;
	
	/**
	 * @parameter default-value="${project.basedir}/src/test/javascript" expression="${jsTestSrcDir}"
	 */
	protected String jsTestSrcDir;
	
	/**
	 * @parameter default-value="js" expression="${packageJavaScriptPath}"
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
	 * @parameter default-value="true" expression="${haltOnFailure}"
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
