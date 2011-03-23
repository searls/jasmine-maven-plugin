package com.github.searls.jasmine.io.scripts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.DirectoryScanner;

public class FindsScriptLocationsInDirectory {

	private ConvertsFileToUriString convertsFileToUriString = new ConvertsFileToUriString();
	
	public List<String> find(File directory, List<String> includes, List<String> excludes) throws IOException {
		List<String> scriptLocations = new ArrayList<String>();
		if(directory.canRead()) {
			DirectoryScanner directoryScanner = new DirectoryScanner();
			directoryScanner.setBasedir(directory);		
			directoryScanner.setIncludes(includes.toArray(new String[]{}));
			directoryScanner.setExcludes(excludes.toArray(new String[]{}));
			directoryScanner.addDefaultExcludes();
			directoryScanner.scan();
			
			for(String script : directoryScanner.getIncludedFiles()) {
				scriptLocations.add(convertsFileToUriString.convert(new File(directory,script)));
			}
		}
		return scriptLocations;
	}	
}
