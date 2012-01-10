package com.github.searls.jasmine.runner;

public interface SpecRunnerHtmlGenerator {
	String DEFAULT_SOURCE_ENCODING = "UTF-8";
	String  JASMINE_JS = "/vendor/js/jasmine.js";
	String  JASMINE_HTML_JS = "/vendor/js/jasmine-html.js";
	String  JASMINE_CSS = "/vendor/css/jasmine.css";

	String generate();
	String generateWitRelativePaths();
}
