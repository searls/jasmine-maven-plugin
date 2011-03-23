package com.github.searls.jasmine.io.scripts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ResolvesCompleteListOfScriptLocations {

	private FindsScriptLocationsInDirectory findsScriptLocationsInDirectory = new FindsScriptLocationsInDirectory();
	private ResolvesLocationOfPreloadSources resolvesLocationOfPreloadSources = new ResolvesLocationOfPreloadSources();
	
	public Set<String> resolve(File sourceBaseDir, File specBaseDir, List<String> sourceIncludes, List<String> sourceExcludes, List<String> specIncludes, List<String> specExcludes) throws IOException {
		return resolveWithPreloadSources(sourceBaseDir, specBaseDir, sourceIncludes, sourceExcludes, specIncludes, specExcludes, new ArrayList<String>());
	}
	
	public Set<String> resolveWithPreloadSources(File sourceBaseDir, File specBaseDir, List<String> sourceIncludes, List<String> sourceExcludes, List<String> specIncludes, List<String> specExcludes, List<String> preloadSources) throws IOException {
		Set<String> scripts =  new LinkedHashSet<String>();
		List<String> sourceScripts = findsScriptLocationsInDirectory.find(sourceBaseDir,sourceIncludes,sourceExcludes);
		List<String> specScripts = findsScriptLocationsInDirectory.find(specBaseDir,specIncludes,specExcludes);
		scripts.addAll(resolvesLocationOfPreloadSources.resolve(preloadSources, sourceBaseDir, specBaseDir));
		scripts.addAll(sourceScripts);
		scripts.addAll(specScripts);
		return scripts;
	}
	
}
