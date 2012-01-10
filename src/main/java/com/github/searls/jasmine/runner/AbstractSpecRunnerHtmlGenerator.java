package com.github.searls.jasmine.runner;

import com.github.searls.jasmine.format.FormatsScriptTags;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public abstract class AbstractSpecRunnerHtmlGenerator {
	private static final String SOURCE_ENCODING = "sourceEncoding";
	private static final String CSS_DEPENDENCIES_TEMPLATE_ATTR_NAME = "cssDependencies";
	private static final String JAVASCRIPT_DEPENDENCIES_TEMPLATE_ATTR_NAME = "javascriptDependencies";
	protected static final String SOURCES_TEMPLATE_ATTR_NAME = "sources";
	protected static final String REPORTER_ATTR_NAME = "reporter";
	private HtmlGeneratorConfiguration configuration;
	private FormatsScriptTags formatsScriptTags = new FormatsScriptTags();

	protected AbstractSpecRunnerHtmlGenerator(HtmlGeneratorConfiguration configuration) {
		this.configuration = configuration;
	}

	protected void setEncoding(HtmlGeneratorConfiguration htmlGeneratorConfiguration, StringTemplate template) {
		template.setAttribute(SOURCE_ENCODING, StringUtils.isNotBlank(htmlGeneratorConfiguration.getSourceEncoding()) ? htmlGeneratorConfiguration.getSourceEncoding() : SpecRunnerHtmlGenerator.DEFAULT_SOURCE_ENCODING);
	}

	protected StringTemplate resolveHtmlTemplate() throws IOException {
		String htmlTemplate = configuration.getRunnerTemplate(getDefaultHtmlTemplatePath());
		return new StringTemplate(htmlTemplate, DefaultTemplateLexer.class);
	}

	protected void includeJavaScriptDependencies(List<String> dependencies, StringTemplate template) throws IOException {
		StringBuilder js = new StringBuilder();
		for (String jsFile : dependencies) {
			if(jsFile != null) {
				js.append("<script type=\"text/javascript\">").append(configuration.IOtoString(jsFile)).append("</script>");
			}
		}
		template.setAttribute(JAVASCRIPT_DEPENDENCIES_TEMPLATE_ATTR_NAME, js.toString());
	}

	protected void applyCssToTemplate(List<String> styles, StringTemplate template) throws IOException {
		StringBuilder css = new StringBuilder();
		for (String cssFile : styles) {
			css.append("<style type=\"text/css\">").append(configuration.IOtoString(cssFile)).append("</style>");
		}
		template.setAttribute(CSS_DEPENDENCIES_TEMPLATE_ATTR_NAME, css.toString());
	}

	public HtmlGeneratorConfiguration getConfiguration() {
		return configuration;
	}

	protected abstract String getDefaultHtmlTemplatePath();

	protected void applyScriptTagsToTemplate(String sourcesTemplateAttrName, Set<String> scripts, StringTemplate template) throws IOException {
		template.setAttribute(sourcesTemplateAttrName, formatsScriptTags.format(scripts));
	}
}
