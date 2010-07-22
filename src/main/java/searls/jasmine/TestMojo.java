package searls.jasmine;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

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
				String runnerPath = writeSpecRunnerToOutputDirectory();
				result = new SpecRunnerExecutor().execute(runnerPath);
			} catch (Exception e) {
				throw new MojoFailureException(e,"JavaScript Test execution failed.","Failed to execute generated SpecRunner.html");
			}
			logResults(result);
			if(haltOnFailure && !result.didPass()) {
				throw new MojoFailureException("There were test failures.");
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

	private String writeSpecRunnerToOutputDirectory() throws IOException {
		SpecRunnerHtmlGenerator htmlGenerator = new SpecRunnerHtmlGenerator(preloadSources,jasmineTargetDir+File.separatorChar+srcDirectoryName,jasmineTargetDir+File.separatorChar+specDirectoryName);
		String html = htmlGenerator.generate(pluginArtifacts, ReporterType.JsApiReporter);
		
		getLog().debug("Writing out Spec Runner HTML " + html + " to directory " + jasmineTargetDir);
		String runnerFilePath = FileUtils.catPath(jasmineTargetDir+File.separatorChar,specRunnerHtmlFileName);
		FileUtils.fileWrite(runnerFilePath, html);
		return runnerFilePath;
		
	}

}
