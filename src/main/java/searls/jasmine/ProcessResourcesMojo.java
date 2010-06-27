package searls.jasmine;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal resources
 * @phase process-resources
 */
public class ProcessResourcesMojo extends AbstractJasmineMojo {
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			getLog().info("Processing JavaScript Sources");
			if(srcDir != null && srcDir.exists() && srcDir.isDirectory()) {
				File targetSrcDirectory = new File(jasmineTargetDir.getAbsolutePath()+File.separatorChar+srcDirectoryName);
				targetSrcDirectory.mkdirs();
				FileUtils.copyDirectory(srcDir, targetSrcDirectory);
			} else {
				getLog().warn("JavaScript source folder was expected but was not found. Set configuration property `srcDir` to the directory containing your JavaScript sources. Skipping jasmine:resources processing.");
			}
		} catch (IOException e) {
			throw new MojoFailureException("Failed to copy JavaScript sources.");
		}
	}

}
