package com.github.searls.jasmine.io.scripts;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.searls.jasmine.model.ScriptSearch;

public class ResolvesCompleteListOfScriptLocations {

	private FindsScriptLocationsInDirectory findsScriptLocationsInDirectory = new FindsScriptLocationsInDirectory();
	private ResolvesLocationOfPreloadSources resolvesLocationOfPreloadSources = new ResolvesLocationOfPreloadSources();
	private RelativizesASetOfScripts relativizesASetOfScripts = new RelativizesASetOfScripts();
	private Set<String> preloadScripts;
	private Set<String> sources;
	private Set<String> specs;
	private Set<String> allScripts;

	public Set<String> resolve(ScriptSearch sources, ScriptSearch specs, List<String> preloadSources) throws IOException {
		this.preloadScripts = new LinkedHashSet<String>(resolvesLocationOfPreloadSources.resolve(preloadSources, sources.getDirectory(), specs.getDirectory()));
		this.sources = new LinkedHashSet<String>(findsScriptLocationsInDirectory.find(sources));
		this.specs = new LinkedHashSet<String>(findsScriptLocationsInDirectory.find(specs));

		allScripts = new LinkedHashSet<String>();

		allScripts.addAll(this.preloadScripts);
		allScripts.addAll(this.sources);
		allScripts.addAll(this.specs);
		return allScripts;
	}

	public Set<String> getPreloadScripts() {
		return preloadScripts;
	}

	public Set<String> getSources() {
		return sources;
	}

	public Set<String> getSpecs() {
		return specs;
	}

	public Set<String> getAllScripts() {
		return allScripts;
	}
}
