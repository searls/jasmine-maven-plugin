package com.github.searls.jasmine;

import java.io.File;
import java.io.IOException;

import com.github.searls.jasmine.coffee.CompilesAllCoffeeInDirectory;
import com.github.searls.jasmine.io.DirectoryCopier;

/**
 * TODO - this class really duplicates code from ProcessResourcesMojo at this point.. extract time.
 * 
 * @goal testResources
 * @phase process-test-resources
 */
public class ProcessTestResourcesMojo extends AbstractJasmineMojo {

	public static final String MISSING_DIR_WARNING = 
		"JavaScript test source folder was expected but was not found. " +
		"Set configuration property `jsTestSrcDir` to the directory containing your specs. " +
		"Skipping jasmine:testResources processing.";
	
	private DirectoryCopier directoryCopier = new DirectoryCopier();
	private CompilesAllCoffeeInDirectory compilesAllCoffeeInDirectory = new CompilesAllCoffeeInDirectory();
	
	public void run() throws IOException {
		getLog().info("Processing JavaScript Specs");
		if (specs.getDirectory().exists()) {
			File destination = new File(jasmineTargetDir, specDirectoryName);
			directoryCopier.copyDirectory(specs.getDirectory(), destination);
			compilesAllCoffeeInDirectory.compile(destination, coffeeBareOption);
		} else {
			getLog().warn(MISSING_DIR_WARNING);
		}
	}

}
