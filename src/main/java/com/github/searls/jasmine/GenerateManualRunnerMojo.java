package com.github.searls.jasmine;

import java.io.IOException;

/**
 * @component
 * @goal generateManualRunner
 * @phase generate-sources
 */
public class GenerateManualRunnerMojo extends AbstractJasmineMojo {

	public void run() throws IOException {
  try {
  System.out.println("In generateManualRunnerMojo");
      System.out.println("sources: " + sources.getDirectory().getCanonicalPath());
      System.out.println("jsTestSrcDir: " + specs.getDirectory().getCanonicalPath());
  } catch(Exception e) {
        e.printStackTrace();
  }
  
		if(writingAManualSpecRunnerIsNecessary()) {
			getLog().info("Generating runner '"+manualSpecRunnerHtmlFileName+"' in the Jasmine plugin's target directory to open in a browser to facilitate faster feedback.");
			new CreatesManualRunner(this).create();
		} else {
			getLog().warn("Skipping manual spec runner generation. Check to make sure that both JavaScript directories `"+sources.getDirectory().getAbsolutePath()+"` and `"+specs.getDirectory().getAbsolutePath()+"` exist.");

		}
	}
	
	private boolean writingAManualSpecRunnerIsNecessary() {
		return sources.getDirectory().exists() && specs.getDirectory().exists();
	}

}
