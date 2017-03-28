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

public class ContextPathScriptResolver implements ScriptResolver {

  private final ScriptResolver scriptResolver;
  private final String baseContextPath;
  private final String sourceContextPath;
  private final String specContextPath;

  public ContextPathScriptResolver(ScriptResolver scriptResolver,
                                   String sourceContextPath,
                                   String specContextPath) {
    this(scriptResolver, "", sourceContextPath, specContextPath);
  }

  public ContextPathScriptResolver(ScriptResolver scriptResolver,
                                   String baseContextPath,
                                   String sourceContextPath,
                                   String specContextPath) {
    this.scriptResolver = scriptResolver;
    this.baseContextPath = baseContextPath;
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
    return this.baseContextPath;
  }

  @Override
  public Set<String> getSources() throws ScriptResolverException {
    return relativeToContextPath(
      this.scriptResolver.getSourceDirectory(),
      this.sourceContextPath,
      this.scriptResolver.getSources());
  }

  @Override
  public Set<String> getSpecs() throws ScriptResolverException {
    return relativeToContextPath(
      this.scriptResolver.getSpecDirectory(),
      this.specContextPath,
      this.scriptResolver.getSpecs());
  }

  @Override
  public Set<String> getPreloads() throws ScriptResolverException {
    Set<String> scripts = this.scriptResolver.getPreloads();
    scripts = relativeToContextPath(
      this.scriptResolver.getSourceDirectory(),
      this.sourceContextPath,
      scripts);
    scripts = relativeToContextPath(
      this.scriptResolver.getSpecDirectory(),
      this.specContextPath,
      scripts);
    scripts = relativeToContextPath(
      this.scriptResolver.getBaseDirectory(),
      this.baseContextPath,
      scripts);
    return scripts;
  }

  @Override
  public Set<String> getAllScripts() throws ScriptResolverException {
    LinkedHashSet<String> allScripts = new LinkedHashSet<String>();
    allScripts.addAll(this.getPreloads());
    allScripts.addAll(this.getSources());
    allScripts.addAll(this.getSpecs());
    return allScripts;
  }

  private Set<String> relativeToContextPath(String realPath, String contextPath, Set<String> absoluteScripts) {
    Set<String> relativeScripts = new LinkedHashSet<String>();
    for (String absoluteScript : absoluteScripts) {
      relativeScripts.add(absoluteScript.replace(realPath, contextPath));
    }
    return relativeScripts;
  }
}
