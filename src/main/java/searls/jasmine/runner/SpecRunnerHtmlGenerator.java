package searls.jasmine.runner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
	
	private static final String RUNNER_HTML_TEMPLATE = 
		"<html>" +
			"<head><title>Jasmine Test Runner</title>" +
				"$"+CSS_DEPENDENCIES_TEMPLATE_ATTR_NAME+"$ " +
				"$"+JAVASCRIPT_DEPENDENCIES_TEMPLATE_ATTR_NAME+"$ " +
				"$"+SOURCES_TEMPLATE_ATTR_NAME+"$ " +
			"</head>" +
			"<body><script type=\"text/javascript\">jasmine.getEnv().addReporter(new jasmine.TrivialReporter()); jasmine.getEnv().execute();</script></body>" +
		"</html>";
	
	private final String sourceDir;
	private final String specDir;
	private List<String> sourcesToLoadFirst;
	private List<String> fileNamesAlreadyWrittenAsScriptTags = new ArrayList<String>();

	public SpecRunnerHtmlGenerator(List<String> sourcesToLoadFirst, String sourceDir,String specDir) {
		this.sourcesToLoadFirst = sourcesToLoadFirst;
		this.sourceDir = sourceDir;
		this.specDir = specDir;
	}

	public String generate(List<Artifact> dependencies) {
		try {
			StringTemplate template = new StringTemplate(RUNNER_HTML_TEMPLATE,DefaultTemplateLexer.class);
			
			includeJavaScriptAndCssDependencies(dependencies, template);

			StringBuilder scriptTags = new StringBuilder();
			appendScriptTagsForFileNames(scriptTags,expandSourcesToLoadFirstRelativeToSourceDir());				
			appendScriptTagsForFileNames(scriptTags, fileNamesForScriptsInDirectory(sourceDir));
			appendScriptTagsForFileNames(scriptTags, fileNamesForScriptsInDirectory(specDir));
			template.setAttribute(SOURCES_TEMPLATE_ATTR_NAME,scriptTags.toString());			
			
			return template.toString();
		} catch (IOException e) {
			throw new RuntimeException("Couldn't open and/or read SpecRunner.html.template",e);
		}
	}

	private List<String> expandSourcesToLoadFirstRelativeToSourceDir() {
		List<String> fullFileNames = new ArrayList<String>();
		if(sourcesToLoadFirst != null) {
			for(String sourceToLoadFirst : sourcesToLoadFirst) {
				fullFileNames.add(sourceDir+File.separatorChar+sourceToLoadFirst);
			}
		}
		return fullFileNames;
	}

	@SuppressWarnings("unchecked")
	private List<String> fileNamesForScriptsInDirectory(String directory) throws IOException {
		List<String> names = new ArrayList<String>();
		if(directory != null) {
			FileUtils.mkdir(directory);
			names = FileUtils.getFileNames(new File(directory), "**/*.js", null, true);
			Collections.sort(names); 
		} 
		return names;
	}

	private void appendScriptTagsForFileNames(StringBuilder sb, List<String> sourceFileNames) {
		for (String sourceFileName : sourceFileNames) {
			if(!fileNamesAlreadyWrittenAsScriptTags.contains(sourceFileName)) {
				sb.append("<script type=\"text/javascript\" src=\"").append(sourceFileName).append("\"></script>");
				fileNamesAlreadyWrittenAsScriptTags.add(sourceFileName);
			}
		}
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
