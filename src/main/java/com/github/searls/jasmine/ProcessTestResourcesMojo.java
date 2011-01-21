package com.github.searls.jasmine;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.github.searls.jasmine.io.DirectoryCopier;


/**
 * @goal testResources
 * @phase process-test-resources
 */
public class ProcessTestResourcesMojo extends AbstractJasmineMojo {
	
	private static final String JS_EXT = ".js";
	
	private DirectoryCopier directoryCopier = new DirectoryCopier();
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			getLog().info("Processing JavaScript Specs");
			if(jsTestSrcDir.exists()) {
				directoryCopier.copyDirectory(jsTestSrcDir, new File(jasmineTargetDir,specDirectoryName), JS_EXT);
			} else {
				getLog().warn("JavaScript test source folder was expected but was not found. Set configuration property `jsTestSrcDir` to the directory containing your specs. Skipping jasmine:testResources processing.");
			}
		} catch (IOException e) {
			throw new MojoFailureException("Failed to copy JavaScript test sources.");
		}
	}

}
