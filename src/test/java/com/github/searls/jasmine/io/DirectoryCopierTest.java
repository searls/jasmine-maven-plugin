package com.github.searls.jasmine.io;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryCopierTest {

	@InjectMocks private DirectoryCopier subject = new DirectoryCopier();
	
	@Mock private FileUtilsWrapper fileUtilsWrapper;
	@Mock private FileFilterUtilsWrapper fileFilterUtilsWrapper;
	
	@Mock private File srcDir;
	@Mock private File destDir;

	@Test
	public void shouldApplyDirectoriesToFilterAfterFileFilter() throws IOException {
		IOFileFilter expected = FileFileFilter.FILE;
		
		subject.copyDirectory(srcDir,destDir);
		
		verify(fileFilterUtilsWrapper).or(DirectoryFileFilter.DIRECTORY, expected);
	}

	@Test
	public void shouldApplyVisibleToFilter() throws IOException {
		IOFileFilter fileFilter = FileFileFilter.FILE;
		IOFileFilter dirFilter = stubOrFilter(fileFilter);
		
		subject.copyDirectory(srcDir,destDir);
		
		verify(fileFilterUtilsWrapper).and(HiddenFileFilter.VISIBLE, dirFilter);
	}
	
	@Test
	public void shouldCopyDirectory() throws IOException {
		IOFileFilter fileFilter = FileFileFilter.FILE;
		IOFileFilter dirFilter = stubOrFilter(fileFilter);
		IOFileFilter visibilityFilter = stubAndFilter(dirFilter);
		
		subject.copyDirectory(srcDir,destDir);
		
		verify(fileUtilsWrapper).copyDirectory(srcDir, destDir, visibilityFilter);
	}

	private IOFileFilter stubOrFilter(IOFileFilter fileFilter) {
		IOFileFilter orFilter = mock(IOFileFilter.class);
		when(fileFilterUtilsWrapper.or(eq(DirectoryFileFilter.DIRECTORY), eq(fileFilter))).thenReturn(orFilter);
		return orFilter;
	}
	
	private IOFileFilter stubAndFilter(IOFileFilter first) {
		IOFileFilter andResult = mock(IOFileFilter.class);
		when(fileFilterUtilsWrapper.and(isA(IOFileFilter.class), eq(first))).thenReturn(andResult);
		return andResult;
	}
}
