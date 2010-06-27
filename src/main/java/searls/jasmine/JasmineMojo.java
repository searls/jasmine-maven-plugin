package searls.jasmine;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import searls.jasmine.runner.SpecRunnerHtmlGenerator;

/**
 * @component
 * @execute phase="test"
 * @goal test
 * @phase test
 */
public class JasmineMojo extends AbstractMojo {

		/**
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * @parameter javascriptDirectory
	 */
	private File javascriptDirectory;

	/**
	 * @parameter default-value="${project}"
	 */
	private MavenProject mavenProject;

	/**
	 * @parameter default-value="${plugin.artifacts}"
	 */
	private List<Artifact> pluginArtifacts;

	@SuppressWarnings("unchecked")
	public void execute() throws MojoExecutionException, MojoFailureException {

		getLog().info("Executing Jasmine Tests");

		getLog().info("Printing plugin dependencies:");

		writeSpecRunnerToOutputDirectory();
	}

	private void writeSpecRunnerToOutputDirectory() {
		SpecRunnerHtmlGenerator htmlGenerator = new SpecRunnerHtmlGenerator();
		String html = htmlGenerator.generate(pluginArtifacts);
		try {
			getLog().info("Writing out html "+html+" to directory "+outputDirectory.getAbsolutePath());
			FileUtils.writeStringToFile(new File(outputDirectory,"runner.html"), html);
		} catch (IOException e) {
			new RuntimeException("Failed to write Spec Runner to target directory",e);
		}
	}

}
