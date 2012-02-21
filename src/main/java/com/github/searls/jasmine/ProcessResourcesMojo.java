package com.github.searls.jasmine;

import java.io.File;
import java.io.IOException;

import com.github.searls.jasmine.coffee.CompilesAllCoffeeInDirectory;
import com.github.searls.jasmine.io.DirectoryCopier;

/**
 * @goal resources
 * @phase process-resources
 */
public class ProcessResourcesMojo extends AbstractJasmineMojo {

	public static final String MISSING_DIR_WARNING = 
		"JavaScript source folder was expected but was not found. " +
		"Set configuration property `jsSrcDir` to the directory containing your JavaScript sources. " +
		"Skipping jasmine:resources processing.";
	
	private DirectoryCopier directoryCopier = new DirectoryCopier();
	private CompilesAllCoffeeInDirectory compilesAllCoffeeInDirectory = new CompilesAllCoffeeInDirectory();

	public void run() throws IOException {
		getLog().info("Processing JavaScript Sources");
		if (sources.getDirectory().exists()) {
			File destination = new File(jasmineTargetDir, srcDirectoryName);
			directoryCopier.copyDirectory(sources.getDirectory(), destination);
			compilesAllCoffeeInDirectory.compile(destination, coffeeBareOption);
		} else {
			getLog().warn(MISSING_DIR_WARNING);
		}
	}

}
