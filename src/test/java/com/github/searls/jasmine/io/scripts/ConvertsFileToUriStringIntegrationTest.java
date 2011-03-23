package com.github.searls.jasmine.io.scripts;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.hamcrest.Description;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.rules.ExpectedException;


public class ConvertsFileToUriStringIntegrationTest {
	private ConvertsFileToUriString subject = new ConvertsFileToUriString();
	
	@Rule public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void presentsUrlRepresentationOfFile() throws IOException {
		String expected = "pants";
		File file = File.createTempFile("blerg", expected);
		
		String result = subject.convert(file);
		
		assertThat(result,startsWith("file:"));
		assertThat(result,endsWith(expected));
	}
	
	@Test
	@Ignore("Can't mock URI (final) and can't think of a File instance whose URI would throw malformed URL. Untestable??")
	public void wrapsMalformedUrlExceptionIntoRuntime() {
		expectedException.expect(RuntimeException.class);
		expectedException.expect(new TypeSafeMatcher<RuntimeException>() {
			public boolean matchesSafely(RuntimeException exception) {
				return exception.getCause() instanceof MalformedURLException;
			}
			public void describeTo(Description description) {}
		});
		
		subject.convert(new File("C:\\Program Files\\Acme\\parsers\\acme_full.dtd"));
	}
}
