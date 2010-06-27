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
		File targetSrcDir = new File(jasmineTargetDir.getAbsolutePath()+File.separatorChar+srcDirectoryName);
		if(targetSrcDir != null && targetSrcDir.exists() && targetSrcDir.isDirectory()) {
			getLog().info("Copying processed JavaScript sources into package");
			try {
				File jsPackageDir = new File(packageDir.getAbsolutePath()+File.separatorChar+packageJavaScriptPath);
				FileUtils.copyDirectory(targetSrcDir, jsPackageDir);
			} catch (IOException e) {
				throw new MojoFailureException("Failed to copy processed JavaScript sources into package directory");
			}
		} else {
			getLog().warn("Expected processed JavaScript source files in ${jasmineTargetDir}/${srcDirectoryName}, but directory wasn't found. " +
					"This may result in JavaScript sources being excluded from the package. Skipping jasmine:preparePackage.");
		}

	}

}
