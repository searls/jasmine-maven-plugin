package com.github.searls.jasmine;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.github.searls.jasmine.io.FileUtilsWrapper;
import com.github.searls.jasmine.io.scripts.RelativizesASetOfScripts;
import com.github.searls.jasmine.io.scripts.ResolvesCompleteListOfScriptLocations;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.runner.SpecRunnerHtmlGenerator;


/**
 * @component
 * @goal generateManualRunner
 * @phase generate-sources
 */
public class GenerateManualRunnerMojo extends AbstractJasmineMojo {

	private ResolvesCompleteListOfScriptLocations resolvesCompleteListOfScriptLocations = new ResolvesCompleteListOfScriptLocations();
	private RelativizesASetOfScripts relativizesASetOfScripts = new RelativizesASetOfScripts();
	
	private FileUtilsWrapper fileUtilsWrapper = new FileUtilsWrapper();
	
	public void run() throws IOException {
		if(writingAManualSpecRunnerIsNecessary()) {
			getLog().info("Generating runner '"+manualSpecRunnerHtmlFileName+"' in the Jasmine plugin's target directory to open in a browser to facilitate faster feedback.");
			writeSpecRunnerToSourceSpecDirectory();
		} else {
			getLog().warn("Skipping manual spec runner generation. Check to make sure that both JavaScript directories `"+sources.getDirectory().getAbsolutePath()+"` and `"+specs.getDirectory().getAbsolutePath()+"` exist.");
		}
	}

	private boolean writingAManualSpecRunnerIsNecessary() {
		return sources.getDirectory().exists() && specs.getDirectory().exists();
	}

	private void writeSpecRunnerToSourceSpecDirectory() throws IOException {
		File runnerDestination = new File(jasmineTargetDir,manualSpecRunnerHtmlFileName);
		
		String newRunnerHtml = new SpecRunnerHtmlGenerator(scriptsForRunner(), sourceEncoding).generate(ReporterType.TrivialReporter, customRunnerTemplate);
		if(newRunnerDiffersFromOldRunner(runnerDestination, newRunnerHtml)) {
			saveRunner(runnerDestination, newRunnerHtml);
		} else {
			getLog().info("Skipping spec runner generation, because an identical spec runner already exists.");
		}
	}

	private Set<String> scriptsForRunner() throws IOException {
		return relativizesASetOfScripts.relativize(jasmineTargetDir, resolvesCompleteListOfScriptLocations.resolve(sources, specs, preloadSources));
	}

	private String existingRunner(File destination) throws IOException {
		String existingRunner = null;
		try {
			if(destination.exists()) {
				existingRunner = fileUtilsWrapper.readFileToString(destination);
			}
		} catch(Exception e) {
			getLog().warn("An error occurred while trying to open an existing manual spec runner. Continuing.");
		}
		return existingRunner;
	}

	private boolean newRunnerDiffersFromOldRunner(File runnerDestination, String newRunner) throws IOException {
		return !StringUtils.equals(newRunner, existingRunner(runnerDestination));
	}
	
	private void saveRunner(File runnerDestination, String newRunner) throws IOException {
		fileUtilsWrapper.writeStringToFile(runnerDestination, newRunner, sourceEncoding);
	}
}
