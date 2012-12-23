package com.github.searls.jasmine;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.github.searls.jasmine.io.ScansDirectory;

public class WarOverlay {

	/**
	 * @parameter default-value="src"
	 */
	private String srcDirectoryName;
	
	/**
	 * @parameter 
	 */
	private File jsSrcDir;
	
	/**
	 * @parameter
	 */
	private List<String> sourceIncludes = ScansDirectory.DEFAULT_INCLUDES;
	
	/**
	 * @parameter
	 */
	private List<String> sourceExcludes = Collections.emptyList();

	public String getSrcDirectoryName() {
		return srcDirectoryName;
	}

	public File getJsSrcDir() {
		return jsSrcDir;
	}

	public List<String> getSourceIncludes() {
		return sourceIncludes;
	}

	public List<String> getSourceExcludes() {
		return sourceExcludes;
	}
}
