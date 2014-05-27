package com.github.searls.jasmine.runner;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.codehaus.plexus.util.StringUtils;
import org.stringtemplate.v4.ST;

import com.github.searls.jasmine.format.FormatsScriptTags;

public abstract class AbstractSpecRunnerHtmlGenerator {
  private static final String SOURCE_ENCODING = "sourceEncoding";
  private static final String CSS_DEPENDENCIES_TEMPLATE_ATTR_NAME = "cssDependencies";
  protected static final String JAVASCRIPT_DEPENDENCIES_TEMPLATE_ATTR_NAME = "javascriptDependencies";
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

  protected void applyCssToTemplate(List<String> styles, ST template) throws IOException {
    StringBuilder css = new StringBuilder();
    for (String cssFile : styles) {
      css.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"").append(cssFile).append("\"/>");
    }
    template.add(CSS_DEPENDENCIES_TEMPLATE_ATTR_NAME, css.toString());
  }

  public HtmlGeneratorConfiguration getConfiguration() {
    return configuration;
  }

  protected void applyScriptTagsToTemplate(String sourcesTemplateAttrName, Collection<String> scripts, ST template) throws IOException {
    template.add(sourcesTemplateAttrName, formatsScriptTags.format(scripts));
  }
}
