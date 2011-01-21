package com.github.searls.jasmine;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.github.searls.jasmine.io.DirectoryCopier;


/**
 * @goal resources
 * @phase process-resources
 */
public class ProcessResourcesMojo extends AbstractJasmineMojo {
	
	private static final String JS_EXT = ".js";
	
	private DirectoryCopier directoryCopier = new DirectoryCopier();
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			getLog().info("Processing JavaScript Sources");
			if(jsSrcDir.exists()) {
				directoryCopier.copyDirectory(jsSrcDir, new File(jasmineTargetDir,srcDirectoryName), JS_EXT);
			} else {
				getLog().warn("JavaScript source folder was expected but was not found. Set configuration property `jsSrcDir` to the directory containing your JavaScript sources. Skipping jasmine:resources processing.");
			}
		} catch (IOException e) {
			throw new MojoFailureException("Failed to copy JavaScript sources.");
		}
	}

}
