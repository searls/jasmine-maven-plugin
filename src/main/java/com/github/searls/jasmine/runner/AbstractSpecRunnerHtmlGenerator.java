/*-
 * #%L
 * jasmine-maven-plugin
 * %%
 * Copyright (C) 2010 - 2017 Justin Searls
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.searls.jasmine.runner;

import com.github.searls.jasmine.format.FormatsScriptTags;
import org.codehaus.plexus.util.StringUtils;
import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

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
    return new ST(htmlTemplate, '$', '$');
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
