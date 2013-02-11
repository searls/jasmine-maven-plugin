package com.github.searls.jasmine.runner;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.Set;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.lang3.StringUtils;

import com.github.searls.jasmine.io.scripts.ScriptResolver;

public class DefaultSpecRunnerHtmlGenerator extends AbstractSpecRunnerHtmlGenerator implements SpecRunnerHtmlGenerator {

  protected DefaultSpecRunnerHtmlGenerator(HtmlGeneratorConfiguration configuration) {
    super(configuration);
  }

  public String generate() {
    try {
    	ScriptResolver resolver = getConfiguration().getScriptResolver();
      return generateHtml(
      		resolver.getAllScripts(),
      		resolver.getPreloads(),
      		resolver.getSources(),
      		resolver.getSpecs(),
      		resolver.getSourceDirectory(),
      		resolver.getSpecDirectoryPath()
      		);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load files for dependencies, sources, or a custom runner", e);
    }
  }

  public String generateWitRelativePaths() {
    try {
    	ScriptResolver resolver = getConfiguration().getScriptResolver();
      return generateHtml(
      		resolver.getAllScriptsRelativePath(),
      		resolver.getPreloadsRelativePath(),
      		resolver.getSourcesRelativePath(),
      		resolver.getSpecsRelativePath(),
      		resolver.getSourceDirectory(),
      		resolver.getSpecDirectoryPath()
      		);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load files for dependencies, sources, or a custom runner", e);
    }
  }

  private String generateHtml(Set<String> allScripts,
  														 Set<String> preloads,
  														 Set<String> sources,
  														 Set<String> specs,
  														 String sourceDirectory,
  														 String specDirectory) throws IOException {
    StringTemplate template = resolveHtmlTemplate();
    includeJavaScriptDependencies(asList(JASMINE_JS, JASMINE_HTML_JS), template);
    applyCssToTemplate(asList(JASMINE_CSS), template);
    applyScriptTagsToTemplate("allScriptTags", allScripts, template);
    applyScriptTagsToTemplate("preloadScriptTags", preloads, template);
    applyScriptTagsToTemplate("sourceScriptTags", sources, template);
    applyScriptTagsToTemplate("specScriptTags", specs, template);
    template.setAttribute("allScriptsList", createJsonArray(allScripts));
    template.setAttribute("preloadsList", createJsonArray(preloads));
    template.setAttribute("sourcesList", createJsonArray(sources));
    template.setAttribute("specsList", createJsonArray(specs));
    template.setAttribute("sourceDir", sourceDirectory);
    template.setAttribute("specDir", specDirectory);
    
    setCustomRunnerConfig(template);
    template.setAttribute(REPORTER_ATTR_NAME, getConfiguration().getReporterType().name());
    setEncoding(getConfiguration(), template);

    // these fields are being preserved for backwards compatibility
    applyScriptTagsToTemplate("sources", allScripts, template);
    template.setAttribute("specs", createJsonArray(specs));
    template.setAttribute("priority", createJsonArray(preloads));
    template.setAttribute("requirejsPath", resolveRequirejsPath(sourceDirectory));
    
    return template.toString();
  }

  private String createJsonArray(Set<String> scripts) {
    if (null == scripts || scripts.isEmpty()) {
      return null;
    }
    StringBuilder builder = new StringBuilder("['");
    builder.append(StringUtils.join(scripts,"', '"));
    builder.append("']");
    return builder.toString();
  }

  private void setCustomRunnerConfig(StringTemplate template) throws IOException {
    String customRunnerConfiguration = getConfiguration().getCustomRunnerConfiguration();
    if (null != customRunnerConfiguration) {
      template.setAttribute("customRunnerConfiguration", customRunnerConfiguration);
    }
  }
  
  private String resolveRequirejsPath(String sourceDirectory) {
    String scriptLoaderPath = getConfiguration().getScriptLoaderPath();
    if(null == scriptLoaderPath) {
      return String.format("%s/require.js", sourceDirectory);
    } else {
      return String.format("%s/%s", sourceDirectory, scriptLoaderPath);
    }
  }
}
