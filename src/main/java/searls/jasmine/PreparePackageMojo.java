package searls.jasmine;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

/**
 * @goal preparePackage
 * @phase prepare-package
 */
public class PreparePackageMojo extends AbstractJasmineMojo {
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		File targetSrcDir = new File(jasmineTargetDir,srcDirectoryName);
		if(targetSrcDir.exists()) {
			getLog().info("Copying processed JavaScript sources into package");
			try {
				FileUtils.copyDirectoryStructure(targetSrcDir, new File(packageDir,packageJavaScriptPath));
			} catch (IOException e) {
				throw new MojoFailureException("Failed to copy processed JavaScript sources into package directory");
			}
		} else {
			getLog().warn("Expected processed JavaScript source files in ${jasmineTargetDir}/${srcDirectoryName}, but directory wasn't found. " +
					"This may result in JavaScript sources being excluded from the package. Skipping jasmine:preparePackage.");
		}

	}

}
