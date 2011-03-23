package com.github.searls.jasmine.io;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static java.util.Arrays.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

@SuppressWarnings("unchecked")
public class ScansDirectoryIntegrationTest {

	private static final List<String> DEFAULT_EXCLUDES = Collections.EMPTY_LIST;
	private static final List<String> DEFAULT_INCLUDES = asList("**/*.js");

	private ScansDirectory subject = new ScansDirectory();
	
	private CreatesTempDirectories createsTempDirectories = new CreatesTempDirectories();
	
	private File directory = createsTempDirectories.create("someDir");
	
	@Test
	public void shouldReturnNothingWhenThereIsNothing() {
		List<String> results = subject.scan(directory, DEFAULT_INCLUDES, DEFAULT_EXCLUDES);
		
		assertThat(results,is(DEFAULT_EXCLUDES));
	}
	
	@Test
	public void shouldReturnMatchingJS() {
		String expected = "blah.js";
		createFile(expected);
		
		List<String> results = subject.scan(directory, DEFAULT_INCLUDES, DEFAULT_EXCLUDES);
		
		assertThat(results,hasItem(expected));
	}
	
	@Test
	public void shouldNotReturnSomeHtml() {
		String expected = "blah.html";
		createFile(expected);
		
		List<String> results = subject.scan(directory, DEFAULT_INCLUDES, DEFAULT_EXCLUDES);
		
		assertThat(results,not(hasItem(expected)));
	}
	
	@Test
	public void shouldExcludeExplicitlyExcludedJs() {
		String expected = "pants.js";
		createFile(expected);
		
		List<String> results = subject.scan(directory, DEFAULT_INCLUDES, asList("pants.js"));
		
		assertThat(results,not(hasItem(expected)));
	}
	
	@Test
	public void shouldNotIncludeHiddenSvnFiles() {
		new File(directory,".svn").mkdir();
		String expected = ".svn/logs";
		createFile(expected);
		
		List<String> results = subject.scan(directory, asList("**"), DEFAULT_EXCLUDES);
		
		assertThat(results,not(hasItem(expected)));
	}
	
	private void createFile(String path) {
		try {
			new File(directory,path).createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
