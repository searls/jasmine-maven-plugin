package com.github.searls.jasmine.runner;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.codehaus.plexus.util.StringUtils;
import org.stringtemplate.v4.ST;

import com.github.searls.jasmine.format.FormatsScriptTags;

public abstract class AbstractSpecRunnerHtmlGenerator {
  private static final String SOURCE_ENCODING = "sourceEncoding";
  private static final String CSS_DEPENDENCIES_TEMPLATE_ATTR_NAME = "cssDependencies";
  private static final String JAVASCRIPT_DEPENDENCIES_TEMPLATE_ATTR_NAME = "javascriptDependencies";
  protected static final String SOURCES_TEMPLATE_ATTR_NAME = "sources";
  protected static final String REPORTER_ATTR_NAME = "reporter";
  private final HtmlGeneratorConfiguration configuration;
  private final FormatsScriptTags formatsScriptTags = new FormatsScriptTags();

  protected AbstractSpecRunnerHtmlGenerator(HtmlGeneratorConfiguration configuration) {
    this.configuration = configuration;
  }

  protected void setEncoding(HtmlGeneratorConfiguration htmlGeneratorConfiguration, ST template) {
    template.add(SOURCE_ENCODING, StringUtils.isNotBlank(htmlGeneratorConfiguration.getSourceEncoding()) ? htmlGeneratorConfiguration.getSourceEncoding() : SpecRunnerHtmlGenerator.DEFAULT_SOURCE_ENCODING);
  }

  protected ST resolveHtmlTemplate() throws IOException {
    String htmlTemplate = configuration.getRunnerTemplate();
    return new ST(htmlTemplate,'$','$');
  }

  protected void includeJavaScriptDependencies(List<String> dependencies, ST template) throws IOException {
    StringBuilder js = new StringBuilder();
    for (String jsFile : dependencies) {
      if(jsFile != null) {
        js.append("<script type=\"text/javascript\">").append(configuration.IOtoString(jsFile)).append("</script>");
      }
    }
    template.add(JAVASCRIPT_DEPENDENCIES_TEMPLATE_ATTR_NAME, js.toString());
  }

  protected void applyCssToTemplate(List<String> styles, ST template) throws IOException {
    StringBuilder css = new StringBuilder();
    for (String cssFile : styles) {
      css.append("<style type=\"text/css\">").append(configuration.IOtoString(cssFile)).append("</style>");
    }
    template.add(CSS_DEPENDENCIES_TEMPLATE_ATTR_NAME, css.toString());
  }

  public HtmlGeneratorConfiguration getConfiguration() {
    return configuration;
  }

  protected void applyScriptTagsToTemplate(String sourcesTemplateAttrName, Set<String> scripts, ST template) throws IOException {
    template.add(sourcesTemplateAttrName, formatsScriptTags.format(scripts));
  }
}
