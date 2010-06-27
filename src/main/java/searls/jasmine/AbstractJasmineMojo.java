package searls.jasmine;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

public abstract class AbstractJasmineMojo extends AbstractMojo {

	/** Properties in order of most-to-least interesting for client projects to override **/
	
	/**
	 * @parameter default-value="${project.basedir}/src/main/javascript"
	 */
	protected String srcDir;
	
	/**
	 * @parameter default-value="${project.basedir}/src/test/javascript"
	 */
	protected String testSrcDir;
	
	/**
	 * @parameter default-value="js"
	 */
	protected String packageJavaScriptPath;
	
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
