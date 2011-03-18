package com.github.searls.jasmine;

import java.io.File;
import java.io.IOException;

import com.github.searls.jasmine.io.DirectoryCopier;

/**
 * @goal testResources
 * @phase process-test-resources
 */
public class ProcessTestResourcesMojo extends AbstractJasmineMojo {

	private static final String JS_EXT = ".js";

	private DirectoryCopier directoryCopier = new DirectoryCopier();

	public void run() throws IOException {
		getLog().info("Processing JavaScript Specs");
		if (jsTestSrcDir.exists()) {
			directoryCopier.copyDirectory(jsTestSrcDir, new File(jasmineTargetDir, specDirectoryName), JS_EXT);
		} else {
			getLog().warn("JavaScript test source folder was expected but was not found. " +
					"Set configuration property `jsTestSrcDir` to the directory containing your specs. " +
					"Skipping jasmine:testResources processing.");
		}
	}

}
