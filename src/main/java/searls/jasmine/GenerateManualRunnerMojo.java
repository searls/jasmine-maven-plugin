package searls.jasmine;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

import searls.jasmine.runner.SpecRunnerHtmlGenerator;

/**
 * @component
 * @goal generateManualRunner
 * @phase generate-test-sources
 */
public class GenerateManualRunnerMojo extends AbstractJasmineMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		if(new File(jsSrcDir).exists() && new File(jsTestSrcDir).exists()) {
			getLog().info("Generating a Spec Runner in the project's source test/spec directory that can be executed manually to facilitate faster feedback.");
			try {
				writeSpecRunnerToSourceSpecDirectory();
			} catch (Exception e) {
				throw new MojoFailureException(e,"JavaScript Test execution failed.","Failed to execute generated SpecRunner.html");
			}
		} else {
			getLog().warn("Skipping manual spec runner generation. Check to make sure that both JavaScript directories `"+jsSrcDir+"` and `"+jsTestSrcDir+"` exist.");
		}
	}

	private String writeSpecRunnerToSourceSpecDirectory() throws IOException {
		SpecRunnerHtmlGenerator htmlGenerator = new SpecRunnerHtmlGenerator(preloadSources,jsSrcDir,jsTestSrcDir);
		String html = htmlGenerator.generate(pluginArtifacts);
		
		String runnerFilePath = FileUtils.catPath(jsTestSrcDir+File.separatorChar,specRunnerHtmlFileName);
		FileUtils.fileWrite(runnerFilePath, html);
		return runnerFilePath;
		
	}

}
