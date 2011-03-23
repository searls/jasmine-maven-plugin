package com.github.searls.jasmine;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.github.searls.jasmine.io.scripts.ResolvesCompleteListOfScriptLocations;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.runner.SpecRunnerHtmlGenerator;


/**
 * @component
 * @goal generateManualRunner
 * @phase generate-test-sources
 */
public class GenerateManualRunnerMojo extends AbstractJasmineMojo {

	private ResolvesCompleteListOfScriptLocations resolvesCompleteListOfScriptLocations = new ResolvesCompleteListOfScriptLocations();
	
	public void run() throws IOException {
		if(sources.getDirectory().exists() && specs.getDirectory().exists()) {
			getLog().info("Generating runner '"+manualSpecRunnerHtmlFileName+"' in the Jasmine plugin's target directory to open in a browser to facilitate faster feedback.");
			writeSpecRunnerToSourceSpecDirectory();
		} else {
			getLog().warn("Skipping manual spec runner generation. Check to make sure that both JavaScript directories `"+sources.getDirectory().getAbsolutePath()+"` and `"+specs.getDirectory().getAbsolutePath()+"` exist.");
		}
	}

	private void writeSpecRunnerToSourceSpecDirectory() throws IOException {
		Set<String> scripts = resolvesCompleteListOfScriptLocations.resolve(sources, specs, preloadSources);
		SpecRunnerHtmlGenerator htmlGenerator = new SpecRunnerHtmlGenerator(scripts, sourceEncoding);
		String runner = htmlGenerator.generate(ReporterType.TrivialReporter, customRunnerTemplate);
		
		File destination = new File(jasmineTargetDir,manualSpecRunnerHtmlFileName);
		String existingRunner = loadExistingManualRunner(destination);
		
		if(!StringUtils.equals(runner, existingRunner)) {
			FileUtils.writeStringToFile(destination, runner);
		} else {
			getLog().info("Skipping spec runner generation, because an identical spec runner already exists.");
		}
	}

	private String loadExistingManualRunner(File destination) {
		String existingRunner = null;
		try {
			if(destination.exists()) {
				existingRunner = FileUtils.readFileToString(destination);
			}
		} catch(Exception e) {
			getLog().warn("An error occurred while trying to open an existing manual spec runner. Continuing");
		}
		return existingRunner;
	}

}
