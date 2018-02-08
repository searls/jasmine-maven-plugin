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
package com.github.searls.jasmine.io.scripts;

import java.util.LinkedHashSet;
import java.util.Set;

public class ContextPathScriptResolver extends AbstractScriptResolver {

  private static final String BASE_DIRECTORY = "";

  private final ScriptResolver scriptResolver;
  private final String sourceContextPath;
  private final String specContextPath;

  public ContextPathScriptResolver(ScriptResolver scriptResolver,
                                   String sourceContextPath,
                                   String specContextPath) {
    this.scriptResolver = scriptResolver;
    this.sourceContextPath = sourceContextPath;
    this.specContextPath = specContextPath;
  }

  @Override
  public String getSourceDirectory() {
    return this.sourceContextPath;
  }

  @Override
  public String getSpecDirectory() {
    return this.specContextPath;
  }

  @Override
  public String getBaseDirectory() {
    return BASE_DIRECTORY;
  }

  @Override
  public Set<String> getSources() {
    return relativeToContextPath(
      this.scriptResolver.getSourceDirectory(),
      this.sourceContextPath,
      this.scriptResolver.getSources());
  }

  @Override
  public Set<String> getSpecs() {
    return relativeToContextPath(
      this.scriptResolver.getSpecDirectory(),
      this.specContextPath,
      this.scriptResolver.getSpecs());
  }

  @Override
  public Set<String> getPreloads() {
    Set<String> scripts = this.scriptResolver.getPreloads();
    scripts = relativeToContextPath(
      this.scriptResolver.getSourceDirectory(),
      this.getSourceDirectory(),
      scripts);
    scripts = relativeToContextPath(
      this.scriptResolver.getSpecDirectory(),
      this.getSpecDirectory(),
      scripts);
    scripts = relativeToContextPath(
      this.scriptResolver.getBaseDirectory(),
      this.getBaseDirectory(),
      scripts);
    return scripts;
  }

  private Set<String> relativeToContextPath(String realPath, String contextPath, Set<String> absoluteScripts) {
    Set<String> relativeScripts = new LinkedHashSet<>();
    for (String absoluteScript : absoluteScripts) {
      relativeScripts.add(absoluteScript.replace(realPath, contextPath));
    }
    return relativeScripts;
  }
}
