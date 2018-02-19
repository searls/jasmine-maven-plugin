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
import com.github.searls.jasmine.io.scripts.ScriptResolver;
import org.apache.commons.lang3.StringUtils;
import org.stringtemplate.v4.ST;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Named
public class DefaultSpecRunnerHtmlGenerator implements SpecRunnerHtmlGenerator {

  private static final String SOURCE_ENCODING = "sourceEncoding";
  private static final String CSS_DEPENDENCIES_TEMPLATE_ATTR_NAME = "cssDependencies";
  private static final String JAVASCRIPT_DEPENDENCIES_TEMPLATE_ATTR_NAME = "javascriptDependencies";
  private static final String REPORTER_ATTR_NAME = "reporter";
  private static final String ALL_SCRIPT_TAGS = "allScriptTags";
  private static final String PRELOAD_SCRIPT_TAGS = "preloadScriptTags";
  private static final String SOURCE_SCRIPT_TAGS = "sourceScriptTags";
  private static final String SPEC_SCRIPT_TAGS = "specScriptTags";
  private static final String ALL_SCRIPTS_LIST = "allScriptsList";
  private static final String PRELOADS_LIST = "preloadsList";
  private static final String SOURCES_LIST = "sourcesList";
  private static final String SPECS_LIST = "specsList";
  private static final String SOURCE_DIR = "sourceDir";
  private static final String SPEC_DIR = "specDir";
  private static final String AUTO_REFRESH = "autoRefresh";
  private static final String AUTO_REFRESH_INTERVAL = "autoRefreshInterval";
  private static final String CUSTOM_RUNNER_CONFIGURATION = "customRunnerConfiguration";

  private final FormatsScriptTags formatsScriptTags;

  @Inject
  public DefaultSpecRunnerHtmlGenerator(FormatsScriptTags formatsScriptTags) {
    this.formatsScriptTags = formatsScriptTags;
  }

  @Override
  public String generate(HtmlGeneratorConfiguration configuration) {
    ScriptResolver resolver = configuration.getScriptResolver();
    return this.generateHtml(
      configuration,
      resolver.getAllScripts(),
      resolver.getPreloads(),
      resolver.getSources(),
      resolver.getSpecs(),
      resolver.getSourceDirectory(),
      resolver.getSpecDirectory()
    );
  }

  private String generateHtml(HtmlGeneratorConfiguration configuration,
                              Set<String> allScripts,
                              Set<String> preloads,
                              Set<String> sources,
                              Set<String> specs,
                              String sourceDirectory,
                              String specDirectory) {
    ST template = this.resolveHtmlTemplate(configuration.getRunnerTemplate());
    this.applyScriptTagsToTemplate(
      JAVASCRIPT_DEPENDENCIES_TEMPLATE_ATTR_NAME,
      Arrays.asList(JASMINE_JS, JASMINE_HTML_JS, JASMINE_HTMLSPECFILTER_PATCH_JS, JASMINE_BOOT_JS),
      template);
    this.applyCssToTemplate(Collections.singletonList(JASMINE_CSS), template);
    this.applyScriptTagsToTemplate(ALL_SCRIPT_TAGS, allScripts, template);
    this.applyScriptTagsToTemplate(PRELOAD_SCRIPT_TAGS, preloads, template);
    this.applyScriptTagsToTemplate(SOURCE_SCRIPT_TAGS, sources, template);
    this.applyScriptTagsToTemplate(SPEC_SCRIPT_TAGS, specs, template);
    template.add(ALL_SCRIPTS_LIST, this.createJsonArray(allScripts));
    template.add(PRELOADS_LIST, this.createJsonArray(preloads));
    template.add(SOURCES_LIST, this.createJsonArray(sources));
    template.add(SPECS_LIST, this.createJsonArray(specs));
    template.add(SOURCE_DIR, sourceDirectory);
    template.add(SPEC_DIR, specDirectory);

    template.add(AUTO_REFRESH, configuration.isAutoRefresh());
    template.add(AUTO_REFRESH_INTERVAL, configuration.getAutoRefreshInterval());

    template.add(CUSTOM_RUNNER_CONFIGURATION, configuration.getCustomRunnerConfiguration());
    template.add(REPORTER_ATTR_NAME, configuration.getReporterType().name());
    this.setEncoding(configuration, template);

    return template.render();
  }

  private String createJsonArray(Set<String> scripts) {
    return scripts == null || scripts.isEmpty() ? "[]" : "['" + StringUtils.join(scripts, "', '") + "']";
  }

  private void setEncoding(HtmlGeneratorConfiguration htmlGeneratorConfiguration, ST template) {
    template.add(SOURCE_ENCODING, StringUtils.isNotBlank(htmlGeneratorConfiguration.getSourceEncoding()) ? htmlGeneratorConfiguration.getSourceEncoding() : SpecRunnerHtmlGenerator.DEFAULT_SOURCE_ENCODING);
  }

  private ST resolveHtmlTemplate(String htmlTemplate) {
    return new ST(htmlTemplate, '$', '$');
  }

  private void applyCssToTemplate(List<String> styles, ST template) {
    StringBuilder css = new StringBuilder();
    for (String cssFile : styles) {
      css.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"").append(cssFile).append("\"/>");
    }
    template.add(CSS_DEPENDENCIES_TEMPLATE_ATTR_NAME, css.toString());
  }

  private void applyScriptTagsToTemplate(String sourcesTemplateAttrName, Collection<String> scripts, ST template) {
    template.add(sourcesTemplateAttrName, formatsScriptTags.format(scripts));
  }

}
