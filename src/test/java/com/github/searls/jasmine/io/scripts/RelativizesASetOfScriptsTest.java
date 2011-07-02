package com.github.searls.jasmine.io.scripts;

import static org.mockito.Mockito.*;
import static java.util.Arrays.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.searls.jasmine.io.RelativizesFilePaths;

@RunWith(MockitoJUnitRunner.class)
public class RelativizesASetOfScriptsTest {

	@InjectMocks
	private RelativizesASetOfScripts subject = new RelativizesASetOfScripts();

	@Mock
	private RelativizesFilePaths relativizesFilePaths;

	@Mock
	private File from;

	@Test
	public void loopsThroughStringsAndRelativizes() throws IOException {
		HashSet<String> sources = new HashSet<String>(asList("a", "b"));
		when(relativizesFilePaths.relativize(eq(from), (File) argThat(is(fileNamed("a"))))).thenReturn("alpha");
		when(relativizesFilePaths.relativize(eq(from), (File) argThat(is(fileNamed("b"))))).thenReturn("beta");

		Set<String> result = subject.relativize(from, sources);

		assertThat(result, hasItems("alpha", "beta"));
	}

	private static TypeSafeMatcher<File> fileNamed(final String name) {
		return new TypeSafeMatcher<File>() {
			public boolean matchesSafely(File file) {
				return name.equals(file.getName());
			}
			public void describeTo(Description description) {}
		};	
	}
}
