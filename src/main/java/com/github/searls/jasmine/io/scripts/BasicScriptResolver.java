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

import com.github.searls.jasmine.model.ScriptSearch;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BasicScriptResolver implements ScriptResolver {

  private static final ResolvesLocationOfPreloadSources RESOLVES_PRELOAD_SOURCES = new ResolvesLocationOfPreloadSources();
  private static final FindsScriptLocationsInDirectory FINDS_SCRIPT_LOCATIONS = new FindsScriptLocationsInDirectory();

  private final File baseDirectory;
  private final ScriptSearch sourceScriptSearch;
  private final ScriptSearch specScriptSearch;
  private final List<String> preloadList;

  private Set<String> sources;
  private Set<String> specs;
  private Set<String> preloads;

  public BasicScriptResolver(File baseDirectory,
                             ScriptSearch sourceScriptSearch,
                             ScriptSearch specScriptSearch,
                             List<String> preloadList) {
    this.baseDirectory = baseDirectory;
    this.sourceScriptSearch = sourceScriptSearch;
    this.specScriptSearch = specScriptSearch;
    this.preloadList = preloadList;
    resolveScripts();
  }

  private void resolveScripts() {
    this.preloads = new LinkedHashSet<String>(RESOLVES_PRELOAD_SOURCES.resolve(
      this.preloadList,
      this.sourceScriptSearch.getDirectory(),
      this.specScriptSearch.getDirectory()));
    this.sources = new LinkedHashSet<String>(FINDS_SCRIPT_LOCATIONS.find(this.sourceScriptSearch));
    this.sources.removeAll(this.preloads);

    this.specs = new LinkedHashSet<String>(FINDS_SCRIPT_LOCATIONS.find(this.specScriptSearch));
    this.specs.removeAll(this.preloads);
  }

  @Override
  public String getSourceDirectory() {
    return directoryToString(this.sourceScriptSearch.getDirectory());
  }

  @Override
  public String getSpecDirectory() {
    return directoryToString(this.specScriptSearch.getDirectory());
  }

  @Override
  public String getBaseDirectory() {
    return directoryToString(this.baseDirectory);
  }

  @Override
  public Set<String> getSources() {
    return sources;
  }

  @Override
  public Set<String> getSpecs() {
    return specs;
  }

  @Override
  public Set<String> getPreloads() {
    return preloads;
  }

  @Override
  public Set<String> getAllScripts() {
    LinkedHashSet<String> allScripts = new LinkedHashSet<String>();
    allScripts.addAll(this.getPreloads());
    allScripts.addAll(this.getSources());
    allScripts.addAll(this.getSpecs());
    return allScripts;
  }

  private String directoryToString(File directory) {
    return StringUtils.stripEnd(directory.toURI().toString(), "/");
  }
}
