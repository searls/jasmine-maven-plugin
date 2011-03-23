package com.github.searls.jasmine.runner;

import static java.util.Arrays.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.codehaus.plexus.util.StringUtils;

import com.github.searls.jasmine.format.FormatsScriptTags;
import com.github.searls.jasmine.io.FileUtilsWrapper;
import com.github.searls.jasmine.io.IOUtilsWrapper;

public class SpecRunnerHtmlGenerator {

	public static final String DEFAULT_RUNNER_HTML_TEMPLATE_FILE = "/jasmine-templates/SpecRunner.htmltemplate";
	public static final String DEFAULT_SOURCE_ENCODING = "UTF-8";

	private static final String SOURCE_ENCODING = "sourceEncoding";
	private static final String CSS_DEPENDENCIES_TEMPLATE_ATTR_NAME = "cssDependencies";
	private static final String JAVASCRIPT_DEPENDENCIES_TEMPLATE_ATTR_NAME = "javascriptDependencies";
	private static final String SOURCES_TEMPLATE_ATTR_NAME = "sources";
	private static final String REPORTER_ATTR_NAME = "reporter";

	public static final String  JASMINE_JS = "/vendor/js/jasmine.js";
	public static final String  JASMINE_HTML_JS = "/vendor/js/jasmine-html.js";
	public static final String  JASMINE_CSS = "/vendor/css/jasmine.css";
	
	private FileUtilsWrapper fileUtilsWrapper = new FileUtilsWrapper();
	private IOUtilsWrapper ioUtilsWrapper = new IOUtilsWrapper();
	private FormatsScriptTags formatsScriptTags = new FormatsScriptTags();
	
	private Set<String> scripts;
	private String sourceEncoding;

	public SpecRunnerHtmlGenerator(Set<String> scripts, String sourceEncoding) {
		this.scripts = scripts;
		this.sourceEncoding = sourceEncoding;
	}

	public String generate(ReporterType reporterType, File customRunnerTemplate) {
		try {
			String htmlTemplate = resolveHtmlTemplate(customRunnerTemplate);
			StringTemplate template = new StringTemplate(htmlTemplate, DefaultTemplateLexer.class);

			includeJavaScriptDependencies(asList(JASMINE_JS,JASMINE_HTML_JS), template);
			applyCssToTemplate(asList(JASMINE_CSS), template);
			applyScriptTagsToTemplate(template);
			template.setAttribute(REPORTER_ATTR_NAME, reporterType.name());
			template.setAttribute(SOURCE_ENCODING, StringUtils.isNotBlank(sourceEncoding) ? sourceEncoding : DEFAULT_SOURCE_ENCODING);

			return template.toString();
		} catch (IOException e) {
			throw new RuntimeException("Failed to load files for dependencies, sources, or a custom runner", e);
		}
	}

	private String resolveHtmlTemplate(File customRunnerTemplate) throws IOException {
		return customRunnerTemplate != null ? fileUtilsWrapper.readFileToString(customRunnerTemplate) : ioUtilsWrapper.toString(DEFAULT_RUNNER_HTML_TEMPLATE_FILE);
	}

	private void includeJavaScriptDependencies(List<String> dependencies, StringTemplate template) throws IOException {
		StringBuilder js = new StringBuilder();
		for (String jsFile : dependencies) {
			js.append("<script type=\"text/javascript\">").append(ioUtilsWrapper.toString(jsFile)).append("</script>");
		}
		template.setAttribute(JAVASCRIPT_DEPENDENCIES_TEMPLATE_ATTR_NAME, js.toString());
	}

	private void applyCssToTemplate(List<String> dependencies, StringTemplate template) throws IOException {
		StringBuilder css = new StringBuilder();
		for (String cssFile : dependencies) {
			css.append("<style type=\"text/css\">").append(ioUtilsWrapper.toString(cssFile)).append("</style>");
		}
		template.setAttribute(CSS_DEPENDENCIES_TEMPLATE_ATTR_NAME, css.toString());
	}

	private void applyScriptTagsToTemplate(StringTemplate template) throws IOException {
		template.setAttribute(SOURCES_TEMPLATE_ATTR_NAME, formatsScriptTags.format(scripts));
	}

}
