package com.github.searls.jasmine.io;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FileUtils.class)
public class FileUtilsWrapperTest {

	private FileUtilsWrapper subject = new FileUtilsWrapper();
	
	private File file = mock(File.class);

	@Before
	public void powerfullyMockStaticClasses() {
		mockStatic(FileUtils.class);
	}

	@Test
	public void delegatesReadingFilesToFileUtils() throws IOException {
		String expected = "contents";
		when(FileUtils.readFileToString(file)).thenReturn(expected);

		String result = subject.readFileToString(file);

		assertThat(result,is(expected));
	}
	
	@Test
	public void delegatesForcingMkdirToFileUtils() throws IOException {
		subject.forceMkdir(file);

		verifyStatic();
		FileUtils.forceMkdir(file);
	}	
	
	@Test
	public void listFilesDelegatesToFileUtils() {
		Collection<File> expected = new ArrayList<File>();
		String[] extensions = new String[] {"js"};
		boolean recursive = true;
		when(FileUtils.listFiles(file, extensions, recursive)).thenReturn(expected);
		
		Collection<File> files = subject.listFiles(file,extensions,recursive);
		
		assertThat(files,is(sameInstance(expected)));
	}
	
	@Test
	public void writeStringToFile() throws IOException {
		String data = "string";
		String encoding = "UTF-8";
		
		subject.writeStringToFile(file,data,encoding);
		
		verifyStatic();
		FileUtils.writeStringToFile(file, data,encoding);	
	}
	
	@Test
	public void copyDirectoryShouldDelegate() throws IOException {
		File srcDir = mock(File.class);
		File destDir = mock(File.class);
		IOFileFilter filter = mock(IOFileFilter.class);
		
		subject.copyDirectory(srcDir,destDir,filter);
		
		verifyStatic();
		FileUtils.copyDirectory(srcDir, destDir, filter);
	}
}