package com.github.searls.jasmine.io;

import static java.util.Arrays.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.plexus.util.DirectoryScanner;

public class ScansDirectory {

	public final static String DEFAULT_INCLUDES = "**"+File.separator+"*.js";
	
	private DirectoryScanner directoryScanner = new DirectoryScanner();
	
	public List<String> scan(File directory, List<String> includes, List<String> excludes) {
		Set<String> set = new LinkedHashSet<String>();
		for (String include : includes) {
			set.addAll(performScan(directory, include, excludes));
		}
		return new ArrayList<String>(set);
	}

	private List<String> performScan(File directory, String include, List<String> excludes) {
		directoryScanner.setBasedir(directory);		
		directoryScanner.setIncludes(new String[]{ include });
		directoryScanner.setExcludes(excludes.toArray(new String[]{}));
		directoryScanner.addDefaultExcludes();
		directoryScanner.scan();
		ArrayList<String> result = new ArrayList<String>(asList(directoryScanner.getIncludedFiles()));
		Collections.sort(result);
		return result;
	}

}
