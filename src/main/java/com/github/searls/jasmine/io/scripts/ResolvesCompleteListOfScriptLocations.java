package com.github.searls.jasmine.io.scripts;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.searls.jasmine.model.ScriptSearch;

public class ResolvesCompleteListOfScriptLocations {

	private FindsScriptLocationsInDirectory findsScriptLocationsInDirectory = new FindsScriptLocationsInDirectory();
	private ResolvesLocationOfPreloadSources resolvesLocationOfPreloadSources = new ResolvesLocationOfPreloadSources();
	
	public Set<String> resolve(ScriptSearch sources, ScriptSearch specs, List<String> preloadSources) throws IOException {
		Set<String> scripts =  new LinkedHashSet<String>();
		scripts.addAll(resolvesLocationOfPreloadSources.resolve(preloadSources, sources.getDirectory(), specs.getDirectory()));
		scripts.addAll(findsScriptLocationsInDirectory.find(sources));
		scripts.addAll(findsScriptLocationsInDirectory.find(specs));
		return scripts;
	}
	
}
