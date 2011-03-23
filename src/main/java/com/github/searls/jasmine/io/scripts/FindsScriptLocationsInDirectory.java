package com.github.searls.jasmine.io.scripts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.searls.jasmine.io.ScansDirectory;

public class FindsScriptLocationsInDirectory {

	private ScansDirectory scansDirectory = new ScansDirectory();
	private ConvertsFileToUriString convertsFileToUriString = new ConvertsFileToUriString();
	
	public List<String> find(File directory, List<String> includes, List<String> excludes) throws IOException {
		List<String> scriptLocations = new ArrayList<String>();
		if(directory.canRead()) {
			for(String script : scansDirectory.scan(directory, includes, excludes)) {
				scriptLocations.add(convertsFileToUriString.convert(new File(directory,script)));
			}
		}
		return scriptLocations;
	}
}
