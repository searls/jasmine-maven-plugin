package com.github.searls.jasmine;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoFailureException;

import com.github.searls.jasmine.format.JasmineResultLogger;
import com.github.searls.jasmine.io.scripts.ResolvesCompleteListOfScriptLocations;
import com.github.searls.jasmine.model.JasmineResult;
import com.github.searls.jasmine.model.ScriptSearch;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.runner.SpecRunnerExecutor;
import com.github.searls.jasmine.runner.SpecRunnerHtmlGenerator;


/**
 * @component
 * @goal test
 * @phase test
 * @execute phase="jasmine-process-test-resources"
 */
public class TestMojo extends AbstractJasmineMojo {
	private ResolvesCompleteListOfScriptLocations resolvesCompleteListOfScriptLocations = new ResolvesCompleteListOfScriptLocations();
	
	public void run() throws Exception {
		if(!skipTests) {
			getLog().info("Executing Jasmine Specs");
			File runnerFile = writeSpecRunnerToOutputDirectory();
			JasmineResult result = executeSpecs(runnerFile);
			logResults(result);
			throwAnySpecFailures(result);
		} else {
			getLog().info("Skipping Jasmine Specs");
		}
	}

	private File writeSpecRunnerToOutputDirectory() throws IOException {
		Set<String> scripts = resolvesCompleteListOfScriptLocations.resolve(
				searchForDir(new File(jasmineTargetDir,srcDirectoryName),sources),
				searchForDir(new File(jasmineTargetDir,specDirectoryName),specs),
				preloadSources);
		SpecRunnerHtmlGenerator htmlGenerator = new SpecRunnerHtmlGenerator(scripts, sourceEncoding);
		String html = htmlGenerator.generate(ReporterType.JsApiReporter, customRunnerTemplate);
		
		getLog().debug("Writing out Spec Runner HTML " + html + " to directory " + jasmineTargetDir);
		File runnerFile = new File(jasmineTargetDir,specRunnerHtmlFileName);
		FileUtils.writeStringToFile(runnerFile, html);
		return runnerFile;
	}
	
	private JasmineResult executeSpecs(File runnerFile) throws MalformedURLException {
		JasmineResult result = new SpecRunnerExecutor().execute(
			runnerFile.toURI().toURL(), 
			new File(jasmineTargetDir,junitXmlReportFileName), 
			browserVersion, 
			timeout, debug, getLog(), format);
		return result;
	}

	private void logResults(JasmineResult result) {
		JasmineResultLogger resultLogger = new JasmineResultLogger();
		resultLogger.setLog(getLog());
		resultLogger.log(result);
	}

	private void throwAnySpecFailures(JasmineResult result) throws MojoFailureException {
		if(haltOnFailure && !result.didPass()) {
			throw new MojoFailureException("There were Jasmine spec failures.");
		}
	}	
	
	private ScriptSearch searchForDir(File dir, ScriptSearch search) {
		return new ScriptSearch(dir,search.getIncludes(),search.getExcludes());
	}

}
