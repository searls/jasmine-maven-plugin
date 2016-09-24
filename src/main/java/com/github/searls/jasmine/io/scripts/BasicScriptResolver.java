package com.github.searls.jasmine.io.scripts;

import com.github.searls.jasmine.model.ScriptSearch;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BasicScriptResolver extends AbstractScriptResolver {

  private final File baseDirectory;
  private final ScriptSearch sourceScriptSearch;
  private final ScriptSearch specScriptSearch;

  private final Set<String> sources;
  private final Set<String> specs;
  private final Set<String> preloads;

  public BasicScriptResolver(ResolvesLocationOfPreloadSources resolvesPreloadSources,
                             FindsScriptLocationsInDirectory findsScriptLocations,
                             File baseDirectory,
                             ScriptSearch sourceScriptSearch,
                             ScriptSearch specScriptSearch,
                             List<String> preloadList) {
    this.baseDirectory = baseDirectory;
    this.sourceScriptSearch = sourceScriptSearch;
    this.specScriptSearch = specScriptSearch;

    this.preloads = findPreloadScripts(resolvesPreloadSources, preloadList, this.sourceScriptSearch, this.specScriptSearch);
    this.sources = findWithoutPreloads(findsScriptLocations, this.sourceScriptSearch, this.preloads);
    this.specs = findWithoutPreloads(findsScriptLocations, this.specScriptSearch, this.preloads);
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

  private String directoryToString(File directory) {
    return StringUtils.stripEnd(directory.toURI().toString(), "/");
  }

  private static Set<String> findPreloadScripts(ResolvesLocationOfPreloadSources resolvesPreloadSources,
                                         List<String> preloadList,
                                         ScriptSearch sourceScriptSearch,
                                         ScriptSearch specScriptSearch) {
    return new LinkedHashSet<String>(resolvesPreloadSources.resolve(
      preloadList,
      sourceScriptSearch.getDirectory(),
      specScriptSearch.getDirectory())
    );
  }

  private static Set<String> findWithoutPreloads(FindsScriptLocationsInDirectory findsScriptLocations,
                                                 ScriptSearch scriptSearch,
                                                 Set<String> preloads) {
    Set<String> scripts = new LinkedHashSet<String>(findsScriptLocations.find(scriptSearch));
    scripts.removeAll(preloads);
    return scripts;
  }
}
