package searls.jasmine;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal testResources
 * @phase process-test-resources
 */
public class ProcessTestResourcesMojo extends AbstractJasmineMojo {
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			getLog().info("Processing JavaScript Specs");
			if(testSrcDir != null && testSrcDir.exists() && testSrcDir.isDirectory()) {
				File specDirectory = new File(jasmineTargetDir.getAbsolutePath()+File.separatorChar+specDirectoryName);
				specDirectory.mkdirs();
				FileUtils.copyDirectory(testSrcDir, specDirectory);
			} else {
				getLog().warn("JavaScript test source folder was expected but was not found. Set configuration property `testSrcDir` to the directory containing your specs. Skipping jasmine:testResources processing.");
			}
		} catch (IOException e) {
			throw new MojoFailureException("Failed to copy JavaScript test sources.");
		}
	}

}
