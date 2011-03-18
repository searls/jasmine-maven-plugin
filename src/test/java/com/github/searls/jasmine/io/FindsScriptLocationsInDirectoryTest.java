package com.github.searls.jasmine.io;

import static java.util.Arrays.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.emory.mathcs.backport.java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class FindsScriptLocationsInDirectoryTest {

	@InjectMocks private FindsScriptLocationsInDirectory subject = new FindsScriptLocationsInDirectory();

	@Mock private FileUtilsWrapper fileUtilsWrapper;
	@Mock private ConvertsFileToUriString convertsFileToUriString; 
	
	@Mock private File directory;
	@Mock private File file;
	
	@Test
	public void returnsEmptyCollectionWhenDirIsNull() throws IOException {
		List<String> prepare = subject.find(null);
		
		assertThat(prepare,is(Collections.emptyList()));
	}
	
	@Test
	public void forceMakesTheDirectory() throws IOException {
		subject.find(directory);
		
		verify(fileUtilsWrapper).forceMkdir(directory);
	}

	@Test
	public void addsJsFilesInDirectory() throws IOException {
		String expected = "pants";
		when(fileUtilsWrapper.listFiles(directory, new String[] {"js"}, true)).thenReturn(asList(file));
		when(convertsFileToUriString.convert(file)).thenReturn(expected);
		
		List<String> result = subject.find(directory);
		
		assertThat(result,hasItem(expected));
	}
	
	@Test
	public void sortsFiles() throws IOException {
		String first = "A";
		String second = "B";
		File firstFile = new File(first);
		File secondFile = new File(second);
		when(fileUtilsWrapper.listFiles(directory, new String[] {"js"}, true)).thenReturn(asList(secondFile,firstFile));
		when(convertsFileToUriString.convert(firstFile)).thenReturn(first);
		when(convertsFileToUriString.convert(secondFile)).thenReturn(second);
		
		List<String> result = subject.find(directory);
		
		assertThat(result.get(0),is(first));
		assertThat(result.get(1),is(second));
	}
	
}
