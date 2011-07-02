package com.github.searls.jasmine;

import java.io.IOException;

/**
 * @component
 * @goal generateManualRunner
 * @phase generate-sources
 */
public class GenerateManualRunnerMojo extends AbstractJasmineMojo {

	public void run() throws IOException {
		new CreatesManualRunner(this).create();
	}

}
