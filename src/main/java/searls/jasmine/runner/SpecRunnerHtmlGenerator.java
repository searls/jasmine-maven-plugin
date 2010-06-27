package searls.jasmine.runner;

import java.io.IOException;
import java.util.List;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.util.FileUtils;

public class SpecRunnerHtmlGenerator {
	
	private static String JAVASCRIPT_TYPE = "js";
	private static String JAVASCRIPT_DEPENDENCIES_TEMPLATE_ATTR_NAME = "javascriptDependencies";
	
	private static String CSS_TYPE = "css";
	private static String CSS_DEPENDENCIES_TEMPLATE_ATTR_NAME = "cssDependencies";

	public String generate(List<Artifact> dependencies) {
		try {
			String html =  "<html><head><title>Jasmine Test Runner</title>$cssDependencies$ $javascriptDependencies$ $sources$ $specs$ </head>" +
					"<body><script type=\"text/javascript\">jasmine.getEnv().addReporter(new jasmine.TrivialReporter()); jasmine.getEnv().execute();</script></body></html>";
			StringTemplate template = new StringTemplate(html,DefaultTemplateLexer.class);
			
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

			return template.toString();
		} catch (IOException e) {
			throw new RuntimeException("Couldn't open and/or read SpecRunner.html.template",e);
		}
	}
	
}
