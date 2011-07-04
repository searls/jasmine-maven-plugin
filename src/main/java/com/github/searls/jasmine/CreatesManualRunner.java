package com.github.searls.jasmine;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;

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
	
	private Log log;

	public CreatesManualRunner(AbstractJasmineMojo config) {
		this.config = config;
		log = config.getLog();
	}
	
	public void create() throws IOException {
		File runnerDestination = new File(config.jasmineTargetDir,config.manualSpecRunnerHtmlFileName);
		
		String newRunnerHtml = new SpecRunnerHtmlGenerator(scriptsForRunner(), config.sourceEncoding).generate(ReporterType.TrivialReporter, config.customRunnerTemplate);
		if(newRunnerDiffersFromOldRunner(runnerDestination, newRunnerHtml)) {
			saveRunner(runnerDestination, newRunnerHtml);
		} else {
			log.info("Skipping spec runner generation, because an identical spec runner already exists.");
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
			log.warn("An error occurred while trying to open an existing manual spec runner. Continuing.");
		}
		return existingRunner;
	}

	private boolean newRunnerDiffersFromOldRunner(File runnerDestination, String newRunner) throws IOException {
		return !StringUtils.equals(newRunner, existingRunner(runnerDestination));
	}
	
	private void saveRunner(File runnerDestination, String newRunner) throws IOException {
		fileUtilsWrapper.writeStringToFile(runnerDestination, newRunner, config.sourceEncoding);
	}
	
	public void setLog(Log log) {
		this.log = log;
	}
}
