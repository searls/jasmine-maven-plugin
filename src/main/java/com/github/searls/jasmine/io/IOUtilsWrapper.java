package com.github.searls.jasmine.io;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class IOUtilsWrapper {

	public String toString(InputStream inputStream) throws IOException {
		return IOUtils.toString(inputStream);
	}

	public String toString(String name) throws IOException {
		return toString(getClass().getResourceAsStream(name));
	}

}
