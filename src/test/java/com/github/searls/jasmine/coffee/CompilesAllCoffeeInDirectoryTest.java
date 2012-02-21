package com.github.searls.jasmine.coffee;

import static java.util.Arrays.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.searls.jasmine.io.FileUtilsWrapper;

@RunWith(MockitoJUnitRunner.class)
public class CompilesAllCoffeeInDirectoryTest {
	private static final boolean BARE_OPTION = false;

	@InjectMocks private CompilesAllCoffeeInDirectory subject = new CompilesAllCoffeeInDirectory();
	
	@Mock private FileUtilsWrapper fileUtilsWrapper;
	@Mock private CompilesCoffeeInPlace compilesCoffeeInPlace;
	
	@Mock File directory;
	@Mock File file;
	@Mock File anotherFile;
	
	@Test
	public void compilesEachFile() throws IOException {
		when(fileUtilsWrapper.listFiles(directory, CompilesAllCoffeeInDirectory.COFFEE_EXTENSIONS, true)).thenReturn(asList(file,anotherFile));
		
		subject.compile(directory, BARE_OPTION);
		
		verify(compilesCoffeeInPlace).compile(file, BARE_OPTION);
		verify(compilesCoffeeInPlace).compile(anotherFile, BARE_OPTION);
	}
	
	
}
