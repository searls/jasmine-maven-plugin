package com.github.searls.jasmine.io;

import java.io.File;
import java.io.IOException;

public class CreatesTempDirectories {

	public File create(String name) {
		try {
			File temp = File.createTempFile(name, "");
			temp.delete();
			temp.mkdir();
			return temp;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
