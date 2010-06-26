package searls.jasmine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * @component
 * @execute phase="test"
 * @goal test
 * @phase test
 */
public class JasmineMojo extends AbstractMojo {

	private static String JAVASCRIPT_TYPE = "js";

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
		printJavaScriptDependencies();
	}

	private void printJavaScriptDependencies() {
		for (Artifact dep : pluginArtifacts) {
			if(JAVASCRIPT_TYPE.equals(dep.getType())) { 
				getLog().info(" * "+dep.getGroupId()+":"+dep.getArtifactId()+":"+dep.getVersion()+":"+dep.getType());
				try {
					String js = FileUtils.readFileToString(dep.getFile());
					getLog().info(js);
				} catch (IOException e) {
					throw new RuntimeException("Failed to open file "+dep.getFile().getName(),e);
				}
			}
		}
	}
}
