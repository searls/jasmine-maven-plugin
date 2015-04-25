package com.github.searls.jasmine.runner;

public interface SpecRunnerHtmlGenerator {
	String DEFAULT_SOURCE_ENCODING = "UTF-8";
	String  JASMINE_JS = "/webjars/jasmine/jasmine.js";
	String  JASMINE_HTML_JS = "/webjars/jasmine/jasmine-html.js";
  String  JASMINE_BOOT_JS = "/classpath/lib/boot.js";
	String  JASMINE_CSS = "/webjars/jasmine/jasmine.css";

	String generate();
}
