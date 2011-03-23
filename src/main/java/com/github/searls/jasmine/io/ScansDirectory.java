package com.github.searls.jasmine.io;

import static java.util.Arrays.*;
import java.io.File;
import java.util.List;

import org.codehaus.plexus.util.DirectoryScanner;

public class ScansDirectory {

	public List<String> scan(File directory, List<String> includes, List<String> excludes) {
		DirectoryScanner directoryScanner = new DirectoryScanner();
		directoryScanner.setBasedir(directory);		
		directoryScanner.setIncludes(includes.toArray(new String[]{}));
		directoryScanner.setExcludes(excludes.toArray(new String[]{}));
		directoryScanner.addDefaultExcludes();
		directoryScanner.scan();
		return asList(directoryScanner.getIncludedFiles());
	}

}
