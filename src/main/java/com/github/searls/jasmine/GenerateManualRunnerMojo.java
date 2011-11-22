package com.github.searls.jasmine;

import java.io.IOException;

/**
 * @component
 * @goal generateManualRunner
 * @phase generate-sources
 */
public class GenerateManualRunnerMojo extends AbstractJasmineMojo {

	public void run() throws IOException {
		if(writingAManualSpecRunnerIsNecessary()) {
			getLog().info("Generating runner '"+manualSpecRunnerHtmlFileName+"' in the Jasmine plugin's target directory to open in a browser to facilitate faster feedback.");
			getLog().info("SpecRunnerTemplate" + " " + this.getSpecRunnerTemplate());
			new CreatesManualRunner(this).create();
		} else {
			getLog().warn("Skipping manual spec runner generation. Check to make sure that both JavaScript directories `"+sources.getDirectory().getAbsolutePath()+"` and `"+specs.getDirectory().getAbsolutePath()+"` exist.");

		}
	}
	
	private boolean writingAManualSpecRunnerIsNecessary() {
		return sources.getDirectory().exists() && specs.getDirectory().exists();
	}

}
