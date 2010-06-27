package searls.jasmine;

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

public abstract class AbstractJasmineMojo extends AbstractMojo {

	/**
	 * @parameter default-value="${project.basedir}/src/main/javascript"
	 */
	protected File srcDir;
	
	/**
	 * @parameter default-value="${project.basedir}/src/test/javascript"
	 */
	protected File testSrcDir;
	
	/**
	 * @parameter default-value="${project.build.directory}/jasmine"
	 */
	protected File jasmineTargetDir;
	
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
