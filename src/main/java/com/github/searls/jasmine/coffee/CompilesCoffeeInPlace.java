package com.github.searls.jasmine.coffee;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.github.searls.jasmine.io.FileUtilsWrapper;

public class CompilesCoffeeInPlace {

	private CoffeeScript coffeeScript = new CoffeeScript();
	private FileUtilsWrapper fileUtilsWrapper = new FileUtilsWrapper();
	
	public void compile(File coffeeFile, boolean coffeeBareOption) throws IOException {
		String coffee = fileUtilsWrapper.readFileToString(coffeeFile);
		FileWriter javaScriptWriter = new FileWriter(coffeeFile, false);
		javaScriptWriter.write(coffeeScript.compile(coffee, coffeeBareOption));
		javaScriptWriter.close();
	}
	
}
