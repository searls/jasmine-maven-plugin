package searls.jasmine;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import searls.jasmine.format.JasmineResultLogger;
import searls.jasmine.model.JasmineResult;
import searls.jasmine.runner.SpecRunnerExecutor;
import searls.jasmine.runner.SpecRunnerHtmlGenerator;
import searls.jasmine.runner.SpecRunnerHtmlGenerator.ReporterType;

/**
 * @component
 * @goal test
 * @phase test
 */
public class TestMojo extends AbstractJasmineMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		if(!skipTests) {
			getLog().info("Executing Jasmine Tests");
			JasmineResult result;
			try {
				File runnerFile = writeSpecRunnerToOutputDirectory();
				result = new SpecRunnerExecutor().execute(runnerFile.toURI().toURL());
			} catch (Exception e) {
				throw new MojoExecutionException(e,"There was a problem executing Jasmine specs",e.getMessage());
			}
			logResults(result);
			if(haltOnFailure && !result.didPass()) {
				throw new MojoFailureException("There were Jasmine spec failures.");
			}
		} else {
			getLog().info("Skipping Jasmine Tests");
		}
	}

	private void logResults(JasmineResult result) {
		JasmineResultLogger resultLogger = new JasmineResultLogger();
		resultLogger.setLog(getLog());
		resultLogger.log(result);
	}

	private File writeSpecRunnerToOutputDirectory() throws IOException {
		SpecRunnerHtmlGenerator htmlGenerator = new SpecRunnerHtmlGenerator(preloadSources,new File(jasmineTargetDir,srcDirectoryName),new File(jasmineTargetDir,specDirectoryName));
		String html = htmlGenerator.generate(pluginArtifacts, ReporterType.JsApiReporter);
		
		getLog().debug("Writing out Spec Runner HTML " + html + " to directory " + jasmineTargetDir);
		File runnerFile = new File(jasmineTargetDir,specRunnerHtmlFileName);
		FileUtils.writeStringToFile(runnerFile, html);
		return runnerFile;
	}

}
