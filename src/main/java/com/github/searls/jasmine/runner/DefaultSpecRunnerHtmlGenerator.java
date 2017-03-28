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

import com.github.searls.jasmine.io.scripts.ScriptResolver;
import com.github.searls.jasmine.io.scripts.ScriptResolverException;
import org.apache.commons.lang3.StringUtils;
import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

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
    this.applyScriptTagsToTemplate(
      JAVASCRIPT_DEPENDENCIES_TEMPLATE_ATTR_NAME,
      Arrays.asList(JASMINE_JS, JASMINE_HTML_JS, JASMINE_HTMLSPECFILTER_PATCH_JS, JASMINE_BOOT_JS),
      template);
    this.applyCssToTemplate(Arrays.asList(JASMINE_CSS), template);
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

    template.add("autoRefresh", this.getConfiguration().getAutoRefresh());
    template.add("autoRefreshInterval", this.getConfiguration().getAutoRefreshInterval());

    this.setCustomRunnerConfig(template);
    template.add(REPORTER_ATTR_NAME, this.getConfiguration().getReporterType().name());
    this.setEncoding(this.getConfiguration(), template);

    // these fields are being preserved for backwards compatibility
    this.applyScriptTagsToTemplate("sources", allScripts, template);
    template.add("specs", this.createJsonArray(specs));
    template.add("priority", this.createJsonArray(preloads));

    return template.render();
  }

  private String createJsonArray(Set<String> scripts) {
    if (null == scripts || scripts.isEmpty()) {
      return "[]";
    }
    StringBuilder builder = new StringBuilder("['");
    builder.append(StringUtils.join(scripts, "', '"));
    builder.append("']");
    return builder.toString();
  }

  private void setCustomRunnerConfig(ST template) throws IOException {
    String customRunnerConfiguration = this.getConfiguration().getCustomRunnerConfiguration();
    template.add("customRunnerConfiguration", customRunnerConfiguration);
  }
}
