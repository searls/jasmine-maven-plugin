package com.github.searls.jasmine.runner;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Set;

import static java.util.Arrays.asList;

public class DefaultSpecRunnerHtmlGenerator extends AbstractSpecRunnerHtmlGenerator implements SpecRunnerHtmlGenerator {

  public static final String DEFAULT_RUNNER_HTML_TEMPLATE_FILE = "/jasmine-templates/SpecRunner.htmltemplate";

  protected DefaultSpecRunnerHtmlGenerator(HtmlGeneratorConfiguration configuration) {
    super(configuration);
  }

  public String generate() {
    try {
      return generateHtml(
      		getConfiguration().getAllScripts(),
      		getConfiguration().getPreloads(),
      		getConfiguration().getSources(),
      		getConfiguration().getSpecs()      		
      		);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load files for dependencies, sources, or a custom runner", e);
    }
  }

  public String generateWitRelativePaths() {
    try {
      return generateHtml(
      		getConfiguration().getAllScriptsRelativePath(),
      		getConfiguration().getPreloadsRelativePath(),
      		getConfiguration().getSourcesRelativePath(),
      		getConfiguration().getSpecsRelativePath()      		
      		);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load files for dependencies, sources, or a custom runner", e);
    }
  }

  private String generateHtml(Set<String> allScripts,
  														 Set<String> preloads,
  														 Set<String> sources,
  														 Set<String> specs) throws IOException {
    StringTemplate template = resolveHtmlTemplate();
    includeJavaScriptDependencies(asList(JASMINE_JS, JASMINE_HTML_JS), template);
    applyCssToTemplate(asList(JASMINE_CSS), template);
    applyScriptTagsToTemplate("allScriptTags", allScripts, template);
    applyScriptTagsToTemplate("preloadScriptTags", preloads, template);
    applyScriptTagsToTemplate("sourceScriptTags", sources, template);
    applyScriptTagsToTemplate("specScriptTags", specs, template);
    template.setAttribute("allScripts", createJsonArray(allScripts));
    template.setAttribute("preloads", createJsonArray(preloads));
    template.setAttribute("sources", createJsonArray(sources));
    template.setAttribute("specs", createJsonArray(specs));
    template.setAttribute(REPORTER_ATTR_NAME, getConfiguration().getReporterType().name());
    setEncoding(getConfiguration(), template);

    return template.toString();
  }

  @Override
  protected String getDefaultHtmlTemplatePath() {
    return DEFAULT_RUNNER_HTML_TEMPLATE_FILE;
  }
  
  private String createJsonArray(Set<String> scripts) {
    if (null == scripts || scripts.isEmpty()) {
      return null;
    }
    StringBuilder builder = new StringBuilder("['");
    builder.append(StringUtils.join(scripts,"', '"));
    builder.append("']");
    System.out.println(builder.toString());
    return builder.toString();
  }


}
