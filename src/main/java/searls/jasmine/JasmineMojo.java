package searls.jasmine;

import java.io.File;
import java.util.List;

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
		
		getLog().info("Printing project dependencies:");
		List<Dependency> deps = mavenProject.getDependencies();
		for (Dependency dep : deps) {
			getLog().info(" * "+dep.getGroupId()+":"+dep.getArtifactId()+":"+dep.getVersion()+":"+dep.getType());
		}
		
		getLog().info("Printing plugin dependencies:");
		for (Artifact dep : pluginArtifacts) {
			getLog().info(" * "+dep.getGroupId()+":"+dep.getArtifactId()+":"+dep.getVersion()+":"+dep.getType());
		}
	}


}
