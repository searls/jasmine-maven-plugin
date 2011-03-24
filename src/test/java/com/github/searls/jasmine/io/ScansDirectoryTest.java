package com.github.searls.jasmine.io;

import static java.util.Arrays.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.codehaus.plexus.util.DirectoryScanner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.searls.jasmine.collections.ScriptSorter;

@RunWith(MockitoJUnitRunner.class)
public class ScansDirectoryTest {
	
	@InjectMocks private ScansDirectory subject = new ScansDirectory();
	@Mock private ScriptSorter scriptSorter;
	@Mock private DirectoryScanner directoryScanner;
	
	@Test
	@SuppressWarnings("unchecked")
	public void sortsResults() {
		String expected = "pants";
		List<String> includes = asList("some include patterns");
		when(directoryScanner.getIncludedFiles()).thenReturn(new String[]{expected});
		
		subject.scan(null, includes, Collections.EMPTY_LIST);
		
		verify(scriptSorter).sort(asList(expected), includes);
	}
	
}
