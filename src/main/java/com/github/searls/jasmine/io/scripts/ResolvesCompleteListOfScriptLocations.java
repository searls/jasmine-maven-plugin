package com.github.searls.jasmine.io.scripts;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.searls.jasmine.model.ScriptSearch;

public class ResolvesCompleteListOfScriptLocations {

	private FindsScriptLocationsInDirectory findsScriptLocationsInDirectory = new FindsScriptLocationsInDirectory();
	private ResolvesLocationOfPreloadSources resolvesLocationOfPreloadSources = new ResolvesLocationOfPreloadSources();
	
	public Set<String> resolveWithPreloadSources(ScriptSearch sources, ScriptSearch specs, List<String> preloadSources) throws IOException {
		Set<String> scripts =  new LinkedHashSet<String>();
		List<String> sourceScripts = findsScriptLocationsInDirectory.find(sources);
		List<String> specScripts = findsScriptLocationsInDirectory.find(specs);
		scripts.addAll(resolvesLocationOfPreloadSources.resolve(preloadSources, sources.getBaseDirectory(), specs.getBaseDirectory()));
		scripts.addAll(sourceScripts);
		scripts.addAll(specScripts);
		return scripts;
	}
	
}
