package com.github.searls.jasmine.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FindsScriptLocationsInDirectory {

	private ConvertsFileToUriString convertsFileToUriString = new ConvertsFileToUriString();
	private FileUtilsWrapper fileUtilsWrapper = new FileUtilsWrapper();
	
	public List<String> find(File directory) throws IOException {
		List<String> fileNames = new ArrayList<String>();
		if (directory != null) {
			fileUtilsWrapper.forceMkdir(directory);
			List<File> files = findJavaScriptBeneathDirectory(directory);
			Collections.sort(files);
			appendLocations(fileNames, files);
		}
		return fileNames;
	}

	private ArrayList<File> findJavaScriptBeneathDirectory(File directory) {
		return new ArrayList<File>(fileUtilsWrapper.listFiles(directory, new String[] { "js" }, true));
	}

	private void appendLocations(List<String> fileNames, List<File> files) {
		for (File file : files) {
			fileNames.add(convertsFileToUriString.convert(file));
		}
	}
	
}
