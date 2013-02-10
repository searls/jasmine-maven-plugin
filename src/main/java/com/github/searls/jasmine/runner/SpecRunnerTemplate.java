package com.github.searls.jasmine.runner;

public enum SpecRunnerTemplate {
	DEFAULT("/jasmine-templates/SpecRunner.htmltemplate"),
	REQUIRE_JS("/jasmine-templates/RequireJsSpecRunner.htmltemplate");
	
	private String template;
	
	private SpecRunnerTemplate(String template) {
		this.template = template;
	}
	
	public String getTemplate() {
		return this.template;
	}
}
