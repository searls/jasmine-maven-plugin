package com.github.searls.jasmine;

import java.io.File;
import java.io.IOException;

import com.github.searls.jasmine.io.DirectoryCopier;

/**
 * @goal resources
 * @phase process-resources
 */
public class ProcessResourcesMojo extends AbstractJasmineMojo {

	private DirectoryCopier directoryCopier = new DirectoryCopier();

	public void run() throws IOException {
		getLog().info("Processing JavaScript Sources");
		if (sources.getDirectory().exists()) {
			directoryCopier.copyDirectory(sources.getDirectory(), new File(jasmineTargetDir, srcDirectoryName));
		} else {
			getLog().warn("JavaScript source folder was expected but was not found. " +
					"Set configuration property `jsSrcDir` to the directory containing your JavaScript sources. " +
					"Skipping jasmine:resources processing.");
		}
	}

}
