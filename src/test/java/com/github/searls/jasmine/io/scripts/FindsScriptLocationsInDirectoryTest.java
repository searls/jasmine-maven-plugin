package com.github.searls.jasmine.io.scripts;

import static java.util.Arrays.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.searls.jasmine.io.ScansDirectory;
import com.github.searls.jasmine.model.ScriptSearch;

import edu.emory.mathcs.backport.java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class FindsScriptLocationsInDirectoryTest {

	private static final List<String> INCLUDES = asList("So in");
	private static final List<String> EXCLUDES = asList("So out");
	private static final String FILE_LOCATION = "blah/a.js";
	
	@InjectMocks private FindsScriptLocationsInDirectory subject = new FindsScriptLocationsInDirectory();

	@Mock private ScansDirectory scansDirectory;
	@Mock private ConvertsFileToUriString convertsFileToUriString; 
	
	@Spy private File directory = new File("Not quite a real directory");
	
	@Before
	public void directoryStubbing() {
		when(directory.canRead()).thenReturn(true);
	}
	
	@Test
	public void returnsEmptyWhenDirectoryDoesNotExist() throws IOException {
		List<String> result = subject.find(new ScriptSearch(new File("No way does this file exist"),null,null));
		
		assertThat(result,is(Collections.emptyList()));
	}

	@Test
	public void addsScriptLocationScannerFinds() throws IOException {
		String expected = "full blown file";
		when(scansDirectory.scan(directory, INCLUDES, EXCLUDES)).thenReturn(asList(FILE_LOCATION));
		when(convertsFileToUriString.convert(new File(directory,FILE_LOCATION))).thenReturn(expected);
		
		List<String> result = subject.find(new ScriptSearch(directory,INCLUDES,EXCLUDES));
		
		assertThat(result,hasItem(expected));
	}
	
}
