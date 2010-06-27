package searls.jasmine.runner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.util.FileUtils;

public class SpecRunnerHtmlGenerator {
	

	private static final String CSS_TYPE = "css";
	private static final String CSS_DEPENDENCIES_TEMPLATE_ATTR_NAME = "cssDependencies";
	
	private static final String JAVASCRIPT_TYPE = "js";
	private static final String JAVASCRIPT_DEPENDENCIES_TEMPLATE_ATTR_NAME = "javascriptDependencies";
	
	private static final String SOURCES_TEMPLATE_ATTR_NAME = "sources";
	private static final String SPECS_TEMPLATE_ATTR_NAME = "specs";	
	
	private static final String RUNNER_HTML_TEMPLATE = 
		"<html>" +
			"<head><title>Jasmine Test Runner</title>" +
				"$"+CSS_DEPENDENCIES_TEMPLATE_ATTR_NAME+"$ " +
				"$"+JAVASCRIPT_DEPENDENCIES_TEMPLATE_ATTR_NAME+"$ " +
				"$"+SOURCES_TEMPLATE_ATTR_NAME+"$ " +
				"$"+SPECS_TEMPLATE_ATTR_NAME+"$ " +
			"</head>" +
			"<body><script type=\"text/javascript\">jasmine.getEnv().addReporter(new jasmine.TrivialReporter()); jasmine.getEnv().execute();</script></body>" +
		"</html>";
	
	private final String sourceDir;
	private final String specDir;

	public SpecRunnerHtmlGenerator(String sourceDir,String specDir) {
		this.sourceDir = sourceDir;
		this.specDir = specDir;
	}

	public String generate(List<Artifact> dependencies) {
		try {
			StringTemplate template = new StringTemplate(RUNNER_HTML_TEMPLATE,DefaultTemplateLexer.class);
			
			includeJavaScriptAndCssDependencies(dependencies, template);

			populateTemplateForScriptsInDirectory(sourceDir, SOURCES_TEMPLATE_ATTR_NAME, template);
			
			populateTemplateForScriptsInDirectory(specDir, SPECS_TEMPLATE_ATTR_NAME, template);
			
			return template.toString();
		} catch (IOException e) {
			throw new RuntimeException("Couldn't open and/or read SpecRunner.html.template",e);
		}
	}

	@SuppressWarnings("unchecked")
	private void populateTemplateForScriptsInDirectory(
			String directory, String attribute, StringTemplate template)
			throws IOException {
		if(directory != null) {
			FileUtils.mkdir(directory);
			List<String> sourceFileNames = FileUtils.getFileNames(new File(directory), "**/*.js", null, true);
			template.setAttribute(attribute,buildScriptTagsForFileNames(sourceFileNames)); 
		}
	}

	private String buildScriptTagsForFileNames(List<String> sourceFileNames) {
		StringBuilder sb = new StringBuilder();
		for (String sourceFileName : sourceFileNames) {
			sb.append("<script type=\"text/javascript\" src=\"").append(sourceFileName).append("\"></script>");
		}
		String sourcesScriptTags = sb.toString();
		return sourcesScriptTags;
	}

	private void includeJavaScriptAndCssDependencies(
			List<Artifact> dependencies, StringTemplate template)
			throws IOException {
		StringBuilder javaScriptDependencies = new StringBuilder();
		StringBuilder cssDependencies = new StringBuilder();
		for(Artifact dep : dependencies) {
			if(JAVASCRIPT_TYPE.equals(dep.getType())) {
				javaScriptDependencies.append("<script type=\"text/javascript\">").append(FileUtils.fileRead(dep.getFile())).append("</script>");
			} else if(CSS_TYPE.equals(dep.getType())) {
				cssDependencies.append("<style type=\"text/css\">").append(FileUtils.fileRead(dep.getFile())).append("</style>");
			}
		}
		template.setAttribute(JAVASCRIPT_DEPENDENCIES_TEMPLATE_ATTR_NAME, javaScriptDependencies.toString());
		template.setAttribute(CSS_DEPENDENCIES_TEMPLATE_ATTR_NAME, cssDependencies.toString());
	}
	
}
