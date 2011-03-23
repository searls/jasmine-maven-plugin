package com.github.searls.jasmine.model;

import java.io.File;
import java.util.List;

public class ScriptSearch {

	private File baseDirectory;
	private List<String> includes;
	private List<String> excludes;
	
	public ScriptSearch(File baseDirectory, List<String> includes, List<String> excludes) {
		this.baseDirectory = baseDirectory;
		this.includes = includes;
		this.excludes = excludes;
	}
	
	public File getBaseDirectory() {
		return baseDirectory;
	}

	public List<String> getIncludes() {
		return includes;
	}

	public List<String> getExcludes() {
		return excludes;
	}
	
}
