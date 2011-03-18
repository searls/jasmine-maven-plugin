package com.github.searls.jasmine.io;

import static java.util.Arrays.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Test;

public class ResolvesLocationOfPreloadSourcesIntegrationTest {

	private static final String SPEC = "spec";
	private static final String SOURCE = "source";

	private ResolvesLocationOfPreloadSources subject = new ResolvesLocationOfPreloadSources();
	
	private File sourceDir = createTempDir(SOURCE);
	private File specDir = createTempDir(SPEC);
	
	@After
	public void deleteTempDirs() {
		sourceDir.delete();
		specDir.delete();
	}
	
	@Test
	public void returnsListWhenSourcesIsNull() {
		List<String> result = subject.resolve(null, sourceDir, specDir);

		assertThat(result,is(not(nullValue())));
	}
	

	@Test
	public void loadsSourceFileWhenItExistsUnderSourceAndSpec() throws IOException {
		String expected = "panda";
		new File(sourceDir,expected).createNewFile();
		new File(specDir,expected).createNewFile();
		List<String> preloadSources = asList(expected);
		
		List<String> result = subject.resolve(preloadSources ,sourceDir,specDir);
		
		
		assertThat(result.size(),is(1));
		assertThat(result.get(0),containsString(SOURCE));
		assertThat(result.get(0),containsString(expected));
	}
	
	@Test
	public void loadsSpecFileWhenItExistsUnderSpec() throws IOException {
		String expected = "panda";
		new File(specDir,expected).createNewFile();
		List<String> preloadSources = asList(expected);
		
		List<String> result = subject.resolve(preloadSources ,sourceDir,specDir);
		
		
		assertThat(result.size(),is(1));
		assertThat(result.get(0),containsString(SPEC));
		assertThat(result.get(0),containsString(expected));
	}
	
	@Test
	public void printsAsIsWhenItDoesNotExist() {
		String expected = "telnet://woahItsTelnet";
		List<String> preloadSources = asList(expected);
		
		List<String> result = subject.resolve(preloadSources ,sourceDir,specDir);
		
		assertThat(result.size(),is(1));
		assertThat(result.get(0),is(expected));
	}

	private File createTempDir(String name) {
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
