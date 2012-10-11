package com.github.searls.jasmine.io.scripts;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.model.ScriptSearch;

public abstract class AbstractScriptResolver implements ScriptResolver {
  private Set<String> sources;
  private Set<String> specs;
  private Set<String> scriptsToPreload;
  private File baseDir;
  private ScriptSearch scriptSearchSources;
  private ScriptSearch scriptSearchSpecs;
  private List<String> preloads;
  private List<String> preloadPatterns;
  private RelativizesASetOfScripts relativizer = new RelativizesASetOfScripts();
  private RelativizesFilePaths relativizesFilePaths = new RelativizesFilePaths();

  protected AbstractScriptResolver(File projectBaseDir,
  																	ScriptSearch sources,
  																	ScriptSearch specs,
  																	List<String> preloads,
  																	List<String> preloadPatterns) {
  	this.preloads = preloads;
    this.scriptSearchSpecs = specs;
    this.scriptSearchSources = sources;
    this.baseDir = projectBaseDir;
    this.preloadPatterns = preloadPatterns;
  }
  
  public void resolveScripts() throws IOException {
    ResolvesLocationOfPreloadSources resolvesLocationOfPreloadSources = new ResolvesLocationOfPreloadSources();
    FindsScriptLocationsInDirectory findsScriptLocationsInDirectory = new FindsScriptLocationsInDirectory();

    this.scriptsToPreload = new LinkedHashSet<String>(
    		resolvesLocationOfPreloadSources.resolve(
    				preloads,
    				scriptSearchSources.getDirectory(),
    				scriptSearchSpecs.getDirectory()
    		)
    );
    this.sources = new LinkedHashSet<String>(	findsScriptLocationsInDirectory.find(scriptSearchSources));
    this.specs = new LinkedHashSet<String>(findsScriptLocationsInDirectory.find(scriptSearchSpecs));
    
    if (this.preloadPatterns != null) {
	    for (String preloadPattern : this.preloadPatterns) {
	    	Pattern pattern = Pattern.compile(preloadPattern);
	    	preloadMatches(pattern,this.sources);
	    	preloadMatches(pattern,this.specs);
	    }
    }
  }
  
  private void preloadMatches(Pattern pattern, Set<String> scripts) {
  	Set<String> toRemove = new HashSet<String>();
  	
  	for (String script : scripts) {
  		if (pattern.matcher(script).matches()) {
  			this.scriptsToPreload.add(script);
  			toRemove.add(script);
  		}
  	}
  	scripts.removeAll(toRemove);
  }

  public Set<String> getPreloads() {
    return this.scriptsToPreload;
  }

  public Set<String> getSources() {
    return this.sources;
  }

  public Set<String> getSpecs() {
    return this.specs;
  }

  public Set<String> getAllScripts() {
    return addAllScripts(scriptsToPreload, sources, specs);
  }

  public String getSourceDirectory() throws IOException {
    return scriptSearchSources.getDirectory().toURI().toURL().toString();
  }

  public String getSpecDirectoryPath() throws MalformedURLException {
    return scriptSearchSpecs.getDirectory().toURI().toURL().toString();
  }

  public Set<String> getSourcesRelativePath() throws IOException {
    return relativizer.relativize(baseDir, this.sources);
  }

  public Set<String> getSpecsRelativePath() throws IOException {
    return relativizer.relativize(baseDir, this.specs);
  }

  public Set<String> getPreloadsRelativePath() throws IOException {
    return relativizer.relativize(baseDir, this.scriptsToPreload);
  }

  public Set<String> getAllScriptsRelativePath() throws IOException {
    return addAllScripts(getPreloadsRelativePath(), getSourcesRelativePath(), getSpecsRelativePath());
  }

  public String getSourceDirectoryRelativePath() throws IOException {
    return relativizesFilePaths.relativize(baseDir, scriptSearchSources.getDirectory());
  }

  public String getSpecDirectoryRelativePath() throws IOException {
    return relativizesFilePaths.relativize(baseDir, scriptSearchSpecs.getDirectory());
  }

  private Set<String> addAllScripts(Set<String> preloadedSources, Set<String> sources, Set<String> specs) {
    LinkedHashSet<String> allScripts = new LinkedHashSet<String>();
    allScripts.addAll(preloadedSources);
    allScripts.addAll(sources);
    allScripts.addAll(specs);
    return allScripts;
  }
}
