package com.github.searls.jasmine.runner;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.stringtemplate.v4.ST;

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
    ST template = resolveHtmlTemplate();
    includeJavaScriptDependencies(asList(JASMINE_JS, JASMINE_HTML_JS), template);
    applyCssToTemplate(asList(JASMINE_CSS), template);
    applyScriptTagsToTemplate("allScriptTags", allScripts, template);
    applyScriptTagsToTemplate("preloadScriptTags", preloads, template);
    applyScriptTagsToTemplate("sourceScriptTags", sources, template);
    applyScriptTagsToTemplate("specScriptTags", specs, template);
    template.add("allScriptsList", createJsonArray(allScripts));
    template.add("preloadsList", createJsonArray(preloads));
    template.add("sourcesList", createJsonArray(sources));
    template.add("specsList", createJsonArray(specs));
    template.add("sourceDir", sourceDirectory);
    template.add("specDir", specDirectory);
    
    setCustomRunnerConfig(template);
    template.add(REPORTER_ATTR_NAME, getConfiguration().getReporterType().name());
    setEncoding(getConfiguration(), template);

    // these fields are being preserved for backwards compatibility
    applyScriptTagsToTemplate("sources", allScripts, template);
    template.add("specs", createJsonArray(specs));
    template.add("priority", createJsonArray(preloads));
    template.add("requirejsPath", resolveRequirejsPath(sourceDirectory));
    
    return template.render();
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

  private void setCustomRunnerConfig(ST template) throws IOException {
    String customRunnerConfiguration = getConfiguration().getCustomRunnerConfiguration();
    if (null != customRunnerConfiguration) {
      template.add("customRunnerConfiguration", customRunnerConfiguration);
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
