package com.github.searls.jasmine;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.util.FileUtils;

/**
 * @goal preparePackage
 * @phase prepare-package
 */
public class PreparePackageMojo extends AbstractJasmineMojo {

	public void run() throws IOException {
		File targetSrcDir = new File(jasmineTargetDir, srcDirectoryName);
		if (targetSrcDir.exists()) {
			getLog().info("Copying processed JavaScript sources into package");
			FileUtils.copyDirectoryStructure(targetSrcDir, new File(packageDir, packageJavaScriptPath));
		} else {
			getLog().warn("Expected processed JavaScript source files in ${jasmineTargetDir}/${srcDirectoryName}, " +
					"but directory wasn't found. This may result in JavaScript sources being excluded from the " +
					"package. Skipping jasmine:preparePackage.");
		}
	}
}
