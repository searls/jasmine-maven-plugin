package com.github.searls.jasmine.io.scripts;

import static com.github.searls.jasmine.Matchers.*;
import static java.util.Arrays.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.searls.jasmine.model.ScriptSearch;

@RunWith(MockitoJUnitRunner.class)
public class ResolvesCompleteListOfScriptLocationsTest {

	@InjectMocks private ResolvesCompleteListOfScriptLocations subject = new ResolvesCompleteListOfScriptLocations();
	
	@Mock private FindsScriptLocationsInDirectory findsScriptLocationsInDirectory;
	@Mock private ResolvesLocationOfPreloadSources resolvesLocationOfPreloadSources;

	@Mock private ScriptSearch sources;
	@Mock private ScriptSearch specs;
	@Mock private File sourceDir;
	@Mock private File specDir;
	
	@Test
	public void spitsOutAnEmptyCollectionByDefault() throws IOException {
		Set<String> result = subject.resolve(sources, specs, null);
		
		assertThat(result,is(empty()));
	}
	
	@Test
	public void addsSourceScripts() throws IOException {
		String expected = "panda";
		when(findsScriptLocationsInDirectory.find(sources)).thenReturn(asList(expected));
		
		Set<String> result = subject.resolve(sources, specs, null);
		
		assertThat(result,hasItem(expected));
	}
	
	@Test
	public void addsSpecScripts() throws IOException {
		String expected = "panda";
		when(findsScriptLocationsInDirectory.find(specs)).thenReturn(asList(expected));
		
		Set<String> result = subject.resolve(sources, specs, null);
		
		assertThat(result,hasItem(expected));
	}
	
	@Test
	public void addsPreloadSources() throws IOException {
		String preload = "pre";
		String expected = "panda";
		when(sources.getDirectory()).thenReturn(sourceDir);
		when(specs.getDirectory()).thenReturn(specDir);
		when(resolvesLocationOfPreloadSources.resolve(asList(preload),sourceDir,specDir)).thenReturn(asList(expected));
		
		Set<String> result = subject.resolve(sources, specs, asList(preload));
		
		assertThat(result,hasItem(expected));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void ordersPreloadThenSourceThenSpec() throws IOException {
		when(resolvesLocationOfPreloadSources.resolve(anyList(),anyFile(),anyFile())).thenReturn(asList("A"));
		when(findsScriptLocationsInDirectory.find(sources)).thenReturn(asList("B"));
		when(findsScriptLocationsInDirectory.find(specs)).thenReturn(asList("C"));
		
		Set<String> result = subject.resolve(sources, specs, asList("blerg"));
		
		Iterator<String> iterator = result.iterator();
		assertThat(iterator.next(),is("A"));
		assertThat(iterator.next(),is("B"));
		assertThat(iterator.next(),is("C"));
	}
	
	@Test
	public void reallyShouldUseALinkedHashSetImplementation() throws IOException {
		Set<String> result = subject.resolve(sources, specs, null);
		
		assertThat(result,is(LinkedHashSet.class));
	}
	
	private File anyFile() {
		return org.mockito.Matchers.any(File.class);
	}
}
