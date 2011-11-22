package com.github.searls.jasmine.runner;

import com.github.searls.jasmine.AbstractJasmineMojo;
import com.github.searls.jasmine.io.scripts.ScriptResolver;

import java.io.IOException;

public class SpecRunnerHtmlGeneratorFactory {

	public static final String DEFAULT = "DEFAULT";
	public static final String REQUIRE_JS = "REQUIRE_JS";

	public SpecRunnerHtmlGenerator create(ReporterType trivialReporter, AbstractJasmineMojo config, ScriptResolver projectDirScripResolver) {
		try {
			return createHtmlGenerator(new HtmlGeneratorConfiguration(trivialReporter, config, projectDirScripResolver));
		} catch (IOException e) {
			throw new InstantiationError(e.getMessage());
		}
	}

	public SpecRunnerHtmlGenerator createHtmlGenerator(HtmlGeneratorConfiguration configuration) throws IllegalArgumentException {
		SpecRunnerHtmlGenerator instance = null;
		String strategy = configuration.getSpecRunnerTemplate();
		if (DEFAULT.equals(strategy)) {
			instance = new DefaultSpecRunnerHtmlGenerator(configuration);
		} else if (REQUIRE_JS.equals(strategy)) {
			instance = new RequireJsSpecRunnerHtmlGenerator(configuration);
		}
		if (null == strategy) {
			throw new IllegalArgumentException("Invalid argument null passed!");
		} else if (instance == null) {
			throw new IllegalArgumentException("Invalid strategy, valid strategies are: " + REQUIRE_JS);
		}
		return instance;
	}
}
