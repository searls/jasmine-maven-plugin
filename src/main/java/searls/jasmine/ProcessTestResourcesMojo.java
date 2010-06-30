package searls.jasmine;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

/**
 * @goal testResources
 * @phase process-test-resources
 */
public class ProcessTestResourcesMojo extends AbstractJasmineMojo {
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			getLog().info("Processing JavaScript Specs");
			if(FileUtils.fileExists(jsTestSrcDir)) {
				FileUtils.copyDirectoryStructure(new File(jsTestSrcDir), new File(jasmineTargetDir+File.separatorChar+specDirectoryName));
			} else {
				getLog().warn("JavaScript test source folder was expected but was not found. Set configuration property `jsTestSrcDir` to the directory containing your specs. Skipping jasmine:testResources processing.");
			}
		} catch (IOException e) {
			throw new MojoFailureException("Failed to copy JavaScript test sources.");
		}
	}

}
