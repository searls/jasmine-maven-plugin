package com.github.searls.jasmine.io.scripts;

import java.io.File;
import java.net.MalformedURLException;

public class ConvertsFileToUriString {

	public String convert(File file) {
		try {
			return file.toURI().toURL().toString();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
}
