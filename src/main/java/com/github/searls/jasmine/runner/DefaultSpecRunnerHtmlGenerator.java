package com.github.searls.jasmine.runner;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.stringtemplate.v4.ST;

import com.github.searls.jasmine.io.scripts.ScriptResolver;
import com.github.searls.jasmine.io.scripts.ScriptResolverException;

public class DefaultSpecRunnerHtmlGenerator extends AbstractSpecRunnerHtmlGenerator implements SpecRunnerHtmlGenerator {

  protected DefaultSpecRunnerHtmlGenerator(HtmlGeneratorConfiguration configuration) {
    super(configuration);
  }

  @Override
  public String generate() {
    try {
      ScriptResolver resolver = this.getConfiguration().getScriptResolver();
      return this.generateHtml(
          resolver.getAllScripts(),
          resolver.getPreloads(),
          resolver.getSources(),
          resolver.getSpecs(),
          resolver.getSourceDirectory(),
          resolver.getSpecDirectory()
          );
    } catch (ScriptResolverException e) {
      throw new RuntimeException("Failed to load files for dependencies, sources, or a custom runner", e);
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
    ST template = this.resolveHtmlTemplate();
    this.includeJavaScriptDependencies(asList(JASMINE_JS, JASMINE_HTML_JS), template);
    this.applyCssToTemplate(asList(JASMINE_CSS), template);
    this.applyScriptTagsToTemplate("allScriptTags", allScripts, template);
    this.applyScriptTagsToTemplate("preloadScriptTags", preloads, template);
    this.applyScriptTagsToTemplate("sourceScriptTags", sources, template);
    this.applyScriptTagsToTemplate("specScriptTags", specs, template);
    template.add("allScriptsList", this.createJsonArray(allScripts));
    template.add("preloadsList", this.createJsonArray(preloads));
    template.add("sourcesList", this.createJsonArray(sources));
    template.add("specsList", this.createJsonArray(specs));
    template.add("sourceDir", sourceDirectory);
    template.add("specDir", specDirectory);

    this.setCustomRunnerConfig(template);
    template.add(REPORTER_ATTR_NAME, this.getConfiguration().getReporterType().name());
    this.setEncoding(this.getConfiguration(), template);

    // these fields are being preserved for backwards compatibility
    this.applyScriptTagsToTemplate("sources", allScripts, template);
    template.add("specs", this.createJsonArray(specs));
    template.add("priority", this.createJsonArray(preloads));
    template.add("requirejsPath", this.resolveRequirejsPath(sourceDirectory));

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
    String customRunnerConfiguration = this.getConfiguration().getCustomRunnerConfiguration();
    template.add("customRunnerConfiguration", customRunnerConfiguration);
  }

  private String resolveRequirejsPath(String sourceDirectory) {
    String scriptLoaderPath = this.getConfiguration().getScriptLoaderPath();
    if(null == scriptLoaderPath) {
      return String.format("%s/require.js", sourceDirectory);
    } else {
      return String.format("%s/%s", sourceDirectory, scriptLoaderPath);
    }
  }
}
