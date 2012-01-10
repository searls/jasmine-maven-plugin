package com.github.searls.jasmine.coffee;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import java.io.File;
import java.io.FileWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.searls.jasmine.io.FileUtilsWrapper;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CompilesCoffeeInPlace.class)
public class CompilesCoffeeInPlaceTest {
	
	private static final String COFFEE = "koohii";
	private static final String JAVA_SCRIPT = "jawa script";
	private static final String BARE_JAVA_SCRIPT = "bare java script";
	private static final boolean BARE_OPTION = false;

	@InjectMocks CompilesCoffeeInPlace subject = new CompilesCoffeeInPlace();
	
	@Mock private CoffeeScript coffeeScript;
	@Mock private FileUtilsWrapper fileUtilsWrapper;
	
	@Mock private File coffeeFile;
	@Mock private FileWriter javaScriptWriter;
	
	@Test
	public void writesCompiledScript() throws Exception {
		when(fileUtilsWrapper.readFileToString(coffeeFile)).thenReturn(COFFEE);
		whenNew(FileWriter.class).withArguments(coffeeFile,false).thenReturn(javaScriptWriter);
		when(coffeeScript.compile(COFFEE, BARE_OPTION)).thenReturn(JAVA_SCRIPT);
		
		subject.compile(coffeeFile, BARE_OPTION);

		verify(javaScriptWriter).write(JAVA_SCRIPT);
		verify(javaScriptWriter).close();
	}

	@Test
	public void writesCoffeeBareCompiledScript() throws Exception {
		when(fileUtilsWrapper.readFileToString(coffeeFile)).thenReturn(COFFEE);
		whenNew(FileWriter.class).withArguments(coffeeFile,false).thenReturn(javaScriptWriter);
		when(coffeeScript.compile(COFFEE, true)).thenReturn(BARE_JAVA_SCRIPT);
		
		subject.compile(coffeeFile, true);

		verify(javaScriptWriter).write(BARE_JAVA_SCRIPT);
		verify(javaScriptWriter).close();
	}
}
