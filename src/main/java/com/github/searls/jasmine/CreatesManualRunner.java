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

public class CreatesManualRunner {

	private ResolvesCompleteListOfScriptLocations resolvesCompleteListOfScriptLocations = new ResolvesCompleteListOfScriptLocations();
	private RelativizesASetOfScripts relativizesASetOfScripts = new RelativizesASetOfScripts();
	
	private FileUtilsWrapper fileUtilsWrapper = new FileUtilsWrapper();
	private AbstractJasmineMojo config;
	
	public CreatesManualRunner(AbstractJasmineMojo config) {
		this.config = config;
	}
	
	public void create() throws IOException {
		if(writingAManualSpecRunnerIsNecessary()) {
			config.getLog().info("Generating runner '"+config.manualSpecRunnerHtmlFileName+"' in the Jasmine plugin's target directory to open in a browser to facilitate faster feedback.");
			writeSpecRunnerToSourceSpecDirectory();
		} else {
			config.getLog().warn("Skipping manual spec runner generation. Check to make sure that both JavaScript directories `"+config.sources.getDirectory().getAbsolutePath()+"` and `"+config.specs.getDirectory().getAbsolutePath()+"` exist.");
		}
	}

	private boolean writingAManualSpecRunnerIsNecessary() {
		return config.sources.getDirectory().exists() && config.specs.getDirectory().exists();
	}

	private void writeSpecRunnerToSourceSpecDirectory() throws IOException {
		File runnerDestination = new File(config.jasmineTargetDir,config.manualSpecRunnerHtmlFileName);
		
		String newRunnerHtml = new SpecRunnerHtmlGenerator(scriptsForRunner(), config.sourceEncoding).generate(ReporterType.TrivialReporter, config.customRunnerTemplate);
		if(newRunnerDiffersFromOldRunner(runnerDestination, newRunnerHtml)) {
			saveRunner(runnerDestination, newRunnerHtml);
		} else {
			config.getLog().info("Skipping spec runner generation, because an identical spec runner already exists.");
		}
	}

	private Set<String> scriptsForRunner() throws IOException {
		return relativizesASetOfScripts.relativize(config.jasmineTargetDir, resolvesCompleteListOfScriptLocations.resolve(config.sources, config.specs, config.preloadSources));
	}

	private String existingRunner(File destination) throws IOException {
		String existingRunner = null;
		try {
			if(destination.exists()) {
				existingRunner = fileUtilsWrapper.readFileToString(destination);
			}
		} catch(Exception e) {
			config.getLog().warn("An error occurred while trying to open an existing manual spec runner. Continuing.");
		}
		return existingRunner;
	}

	private boolean newRunnerDiffersFromOldRunner(File runnerDestination, String newRunner) throws IOException {
		return !StringUtils.equals(newRunner, existingRunner(runnerDestination));
	}
	
	private void saveRunner(File runnerDestination, String newRunner) throws IOException {
		fileUtilsWrapper.writeStringToFile(runnerDestination, newRunner, config.sourceEncoding);
	}
}
