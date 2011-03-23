package com.github.searls.jasmine.io.scripts;

import static com.github.searls.jasmine.collections.CollectionHelper.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ResolvesLocationOfPreloadSources {

	private ConvertsFileToUriString convertsFileToUriString = new ConvertsFileToUriString();

	public List<String> resolve(List<String> preloadSources, File sourceDir, File specDir) {
		List<String> sources = new ArrayList<String>();
		for (String source : list(preloadSources)) {
			if(fileCouldNotBeAdded(new File(sourceDir, source),sources) 
				&& fileCouldNotBeAdded(new File(specDir, source),sources)) {
				sources.add(source);
			}
		}
		return sources;
	}
	
	private boolean fileCouldNotBeAdded(File file, List<String> sourcePaths) {
		boolean canAdd = file.exists();
		if (canAdd) {
			sourcePaths.add(convertsFileToUriString.convert(file));
		}
		return !canAdd;
	}

}
