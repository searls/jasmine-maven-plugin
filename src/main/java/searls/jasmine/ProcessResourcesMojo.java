package searls.jasmine;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

/**
 * @goal resources
 * @phase process-resources
 */
public class ProcessResourcesMojo extends AbstractJasmineMojo {
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			getLog().info("Processing JavaScript Sources");
			if(jsSrcDir.exists()) {
				FileUtils.copyDirectoryStructure(jsSrcDir, new File(jasmineTargetDir,srcDirectoryName));
			} else {
				getLog().warn("JavaScript source folder was expected but was not found. Set configuration property `jsSrcDir` to the directory containing your JavaScript sources. Skipping jasmine:resources processing.");
			}
		} catch (IOException e) {
			throw new MojoFailureException("Failed to copy JavaScript sources.");
		}
	}

}
