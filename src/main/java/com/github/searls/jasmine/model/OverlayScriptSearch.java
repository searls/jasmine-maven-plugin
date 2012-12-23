package com.github.searls.jasmine.model;

import java.io.File;
import java.util.List;

public class OverlayScriptSearch extends ScriptSearch {
	
	private String srcDirectoryName;
	
	public OverlayScriptSearch(String srcDirectoryName, File directory, List<String> includes, List<String> excludes) {
		
		super(directory, includes, excludes);
		this.srcDirectoryName = srcDirectoryName;
	}
	
	public String getSrcDirectoryName() {
		return srcDirectoryName;
	}
}
