package com.github.searls.jasmine.runner;

import static java.util.Arrays.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.codehaus.plexus.util.StringUtils;

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

	//TODO - simplify this by finding all resources by folder instead
	public static final String  JASMINE_JS = "/vendor/js/jasmine.js";
	public static final String  JASMINE_HTML_JS = "/vendor/js/jasmine-html.js";
	public static final String  CONSOLE_X_JS = "/vendor/js/consolex.js";
	public static final String  JSON_2_JS = "/vendor/js/json2.js";
	public static final String  JASMINE_CSS = "/vendor/css/jasmine.css";
	
	private FileUtilsWrapper fileUtilsWrapper = new FileUtilsWrapper();
	private IOUtilsWrapper ioUtilsWrapper = new IOUtilsWrapper();

	private File sourceDir;
	private File specDir;
	private List<String> sourcesToLoadFirst;
	private List<String> fileNamesAlreadyWrittenAsScriptTags = new ArrayList<String>();
	private String sourceEncoding;

	public SpecRunnerHtmlGenerator(File sourceDir, File specDir, List<String> sourcesToLoadFirst, String sourceEncoding) {
		this.sourcesToLoadFirst = sourcesToLoadFirst;
		this.sourceDir = sourceDir;
		this.specDir = specDir;
		this.sourceEncoding = sourceEncoding;
	}

	public String generate(ReporterType reporterType, File customRunnerTemplate) {
		try {
			String htmlTemplate = resolveHtmlTemplate(customRunnerTemplate);
			StringTemplate template = new StringTemplate(htmlTemplate, DefaultTemplateLexer.class);

			includeJavaScriptDependencies(asList(JASMINE_JS,JASMINE_HTML_JS,CONSOLE_X_JS,JSON_2_JS), template);
			includeCssDependencies(asList(JASMINE_CSS), template);
			setJavaScriptSourcesAttribute(template);
			template.setAttribute(REPORTER_ATTR_NAME, reporterType.name());
			template.setAttribute(SOURCE_ENCODING, StringUtils.isNotBlank(sourceEncoding) ? sourceEncoding : DEFAULT_SOURCE_ENCODING);

			return template.toString();
		} catch (IOException e) {
			throw new RuntimeException("Failed to load file names for dependencies or scripts", e);
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

	private void includeCssDependencies(List<String> dependencies, StringTemplate template) throws IOException {
		StringBuilder css = new StringBuilder();
		for (String cssFile : dependencies) {
			css.append("<style type=\"text/css\">").append(ioUtilsWrapper.toString(cssFile)).append("</style>");
		}
		template.setAttribute(CSS_DEPENDENCIES_TEMPLATE_ATTR_NAME, css.toString());
	}

	private void setJavaScriptSourcesAttribute(StringTemplate template) throws IOException {
		StringBuilder scriptTags = new StringBuilder();
		appendScriptTagsForFiles(scriptTags, expandSourcesToLoadFirstRelativeToSourceDir());
		appendScriptTagsForFiles(scriptTags, filesForScriptsInDirectory(sourceDir));
		appendScriptTagsForFiles(scriptTags, filesForScriptsInDirectory(specDir));
		template.setAttribute(SOURCES_TEMPLATE_ATTR_NAME, scriptTags.toString());
	}

	private List<String> expandSourcesToLoadFirstRelativeToSourceDir() {
		List<String> files = new ArrayList<String>();
		if (sourcesToLoadFirst != null) {
			for (String sourceToLoadFirst : sourcesToLoadFirst) {
				File file = new File(sourceDir, sourceToLoadFirst);
				File specFile = new File(specDir, sourceToLoadFirst);
				if (file.exists()) {
					files.add(fileToString(file));
				} else if (specFile.exists()) {
					files.add(fileToString(specFile));
				} else {
					files.add(sourceToLoadFirst);
				}
			}
		}
		return files;
	}

	private List<String> filesForScriptsInDirectory(File directory) throws IOException {
		List<String> fileNames = new ArrayList<String>();
		if (directory != null) {
			fileUtilsWrapper.forceMkdir(directory);
			List<File> files = new ArrayList<File>(fileUtilsWrapper.listFiles(directory, new String[] { "js" }, true));
			Collections.sort(files);
			for (File file : files) {
				fileNames.add(fileToString(file));
			}
		}
		return fileNames;
	}

	private void appendScriptTagsForFiles(StringBuilder sb, List<String> sourceFiles) {
		for (String sourceFile : sourceFiles) {
			if (!fileNamesAlreadyWrittenAsScriptTags.contains(sourceFile)) {
				sb.append("<script type=\"text/javascript\" src=\"").append(sourceFile).append("\"></script>");
				fileNamesAlreadyWrittenAsScriptTags.add(sourceFile);
			}
		}
	}

	private String fileToString(File file) {
		try {
			return file.toURI().toURL().toString();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
