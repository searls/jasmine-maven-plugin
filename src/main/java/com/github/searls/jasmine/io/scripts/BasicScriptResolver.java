package com.github.searls.jasmine.io.scripts;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.plexus.util.StringUtils;

import com.github.searls.jasmine.model.ScriptSearch;

public class BasicScriptResolver implements ScriptResolver {

  private static final ResolvesLocationOfPreloadSources RESOLVES_PRELOAD_SOURCES = new ResolvesLocationOfPreloadSources();
  private static final FindsScriptLocationsInDirectory FINDS_SCRIPT_LOCATIONS = new FindsScriptLocationsInDirectory();

  private final ScriptSearch sourceScriptSearch;
  private final ScriptSearch specScriptSearch;
  private final List<String> preloadList;

  private Set<String> sources;
  private Set<String> specs;
  private Set<String> preloads;

  public BasicScriptResolver(ScriptSearch sourceScriptSearch,
                             ScriptSearch specScriptSearch,
                             List<String> preloadList) {
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
