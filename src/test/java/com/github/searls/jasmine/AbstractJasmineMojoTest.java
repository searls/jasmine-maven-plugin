package com.github.searls.jasmine;

import static org.mockito.Mockito.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.searls.jasmine.exception.StringifiesStackTraces;

@RunWith(MockitoJUnitRunner.class)
public class AbstractJasmineMojoTest {

	@InjectMocks @Spy AbstractJasmineMojo subject = new AbstractJasmineMojo() {
		public void run() throws Exception {}
	};
	@Mock private StringifiesStackTraces stringifiesStackTraces = new StringifiesStackTraces(); 
	
	@Rule public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void executeStringifiesStackTraces() throws Exception {
		String expected = "panda";
		expectedException.expectMessage(expected);
		Exception e = new Exception();
		when(stringifiesStackTraces.stringify(e)).thenReturn(expected);
		doThrow(e).when(subject).run();
		
		subject.execute();
	}
	
}
