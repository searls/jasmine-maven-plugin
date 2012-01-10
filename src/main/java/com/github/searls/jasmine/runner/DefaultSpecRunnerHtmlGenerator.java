package com.github.searls.jasmine.runner;

import org.antlr.stringtemplate.StringTemplate;

import java.io.IOException;
import java.util.Set;

import static java.util.Arrays.asList;

public class DefaultSpecRunnerHtmlGenerator extends AbstractSpecRunnerHtmlGenerator implements SpecRunnerHtmlGenerator {

	public static final String DEFAULT_RUNNER_HTML_TEMPLATE_FILE = "/jasmine-templates/SpecRunner.htmltemplate";

	protected DefaultSpecRunnerHtmlGenerator(HtmlGeneratorConfiguration configuration) {
		super(configuration);
	}

	public String generate() {
		try {
			return generateHtml(getConfiguration().getAllScripts());
		} catch (IOException e) {
			throw new RuntimeException("Failed to load files for dependencies, sources, or a custom runner", e);
		}
	}

	public String generateWitRelativePaths() {
		try {
			Set<String> allScriptsRelativePath = getConfiguration().getAllScriptsRelativePath();
			return generateHtml(allScriptsRelativePath);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load files for dependencies, sources, or a custom runner", e);
		}
	}

	private String generateHtml(Set<String> allScriptsRelativePath) throws IOException {
		StringTemplate template = resolveHtmlTemplate();
		includeJavaScriptDependencies(asList(JASMINE_JS, JASMINE_HTML_JS), template);
		applyCssToTemplate(asList(JASMINE_CSS), template);
		applyScriptTagsToTemplate(SOURCES_TEMPLATE_ATTR_NAME, allScriptsRelativePath, template);
		template.setAttribute(REPORTER_ATTR_NAME, getConfiguration().getReporterType().name());
		setEncoding(getConfiguration(), template);

		return template.toString();
	}

	@Override
	protected String getDefaultHtmlTemplatePath() {
		return DEFAULT_RUNNER_HTML_TEMPLATE_FILE;
	}


}
