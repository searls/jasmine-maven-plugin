package com.github.searls.jasmine.coffee;

import java.io.File;
import java.io.IOException;

import com.github.searls.jasmine.io.FileUtilsWrapper;

public class CompilesAllCoffeeInDirectory {
	
	public static final String[] COFFEE_EXTENSIONS = new String[]{"coffee"};
	
	private FileUtilsWrapper fileUtilsWrapper = new FileUtilsWrapper();
	private CompilesCoffeeInPlace compilesCoffeeInPlace = new CompilesCoffeeInPlace();
	
	public void compile(File directory) throws IOException {
		for(File coffeeFile : fileUtilsWrapper.listFiles(directory, COFFEE_EXTENSIONS, true)) {
			compilesCoffeeInPlace.compile(coffeeFile);
		}
	}
	
}
