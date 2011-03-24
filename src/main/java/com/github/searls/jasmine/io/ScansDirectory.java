package com.github.searls.jasmine.io;

import static java.util.Arrays.*;

import java.io.File;
import java.util.List;

import org.codehaus.plexus.util.DirectoryScanner;

import com.github.searls.jasmine.collections.ScriptSorter;

public class ScansDirectory {

	private DirectoryScanner directoryScanner = new DirectoryScanner();
	private ScriptSorter scriptSorter = new ScriptSorter();
	
	public List<String> scan(File directory, List<String> includes, List<String> excludes) {
		List<String> files = performScan(directory, includes, excludes);
		scriptSorter.sort(files, includes);
		return files;
	}

	private List<String> performScan(File directory, List<String> includes, List<String> excludes) {
		directoryScanner.setBasedir(directory);		
		directoryScanner.setIncludes(includes.toArray(new String[]{}));
		directoryScanner.setExcludes(excludes.toArray(new String[]{}));
		directoryScanner.addDefaultExcludes();
		directoryScanner.scan();
		return asList(directoryScanner.getIncludedFiles());
	}

}
