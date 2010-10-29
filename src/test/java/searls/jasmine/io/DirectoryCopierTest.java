package searls.jasmine.io;

import static org.mockito.Matchers.*;
import static org.powermock.api.mockito.PowerMockito.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import searls.jasmine.io.DirectoryCopier;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value={FileUtils.class,FileFilterUtils.class})
public class DirectoryCopierTest {

	private DirectoryCopier directoryCopier = new DirectoryCopier();
	private File srcDir = mock(File.class);
	private File destDir = mock(File.class);
	
	@Before
	public void before() {
		mockStatic(FileUtils.class);
		mockStatic(FileFilterUtils.class);
	}
	
	@Test
	public void shouldBuildSuffixFilter() throws IOException {
		String suffixFilter = ".js";
		
		directoryCopier.copyDirectory(srcDir,destDir,suffixFilter);
		
		verifyStatic();
		FileFilterUtils.suffixFileFilter(suffixFilter);
	}
	
	@Test
	public void shouldAssignSuffixFilterAsFileFilter() throws IOException {
		IOFileFilter resultSuffixFilter = mock(IOFileFilter.class);
		when(FileFilterUtils.suffixFileFilter(anyString())).thenReturn(resultSuffixFilter);
		
		directoryCopier.copyDirectory(srcDir,destDir,".somethingsomething");
		
		verifyStatic();
		FileFilterUtils.andFileFilter(FileFileFilter.FILE, resultSuffixFilter);
	}
	
	@Test
	public void shouldApplyDirectoriesToFilterAfterFileFilter() throws IOException {
		IOFileFilter resultFileFilter = mock(IOFileFilter.class);
		when(FileFilterUtils.suffixFileFilter(anyString())).thenReturn(mock(IOFileFilter.class));
		when(FileFilterUtils.andFileFilter(isA(IOFileFilter.class), isA(IOFileFilter.class))).thenReturn(resultFileFilter);
		
		directoryCopier.copyDirectory(srcDir,destDir,".somethingsomething");
		
		verifyStatic();
		FileFilterUtils.orFileFilter(DirectoryFileFilter.DIRECTORY, resultFileFilter);
	}
	
	@Test
	public void shouldApplyVisibleToFilter() throws IOException {
		IOFileFilter resultFileFilter = mock(IOFileFilter.class);
		when(FileFilterUtils.suffixFileFilter(anyString())).thenReturn(mock(IOFileFilter.class));
		when(FileFilterUtils.andFileFilter(isA(IOFileFilter.class), isA(IOFileFilter.class))).thenReturn(mock(IOFileFilter.class));
		when(FileFilterUtils.orFileFilter(eq(DirectoryFileFilter.DIRECTORY), isA(IOFileFilter.class))).thenReturn(resultFileFilter);
		
		directoryCopier.copyDirectory(srcDir,destDir,".somethingsomething");
		
		verifyStatic();
		FileFilterUtils.andFileFilter(HiddenFileFilter.VISIBLE, resultFileFilter);
	}
	
	@Test
	public void shouldCopyDirectory() throws IOException {
		IOFileFilter resultFilter = mock(IOFileFilter.class);
		when(FileFilterUtils.suffixFileFilter(anyString())).thenReturn(mock(IOFileFilter.class));
		when(FileFilterUtils.andFileFilter(isA(IOFileFilter.class), isA(IOFileFilter.class))).thenReturn(mock(IOFileFilter.class));
		when(FileFilterUtils.orFileFilter(eq(DirectoryFileFilter.DIRECTORY), isA(IOFileFilter.class))).thenReturn(mock(IOFileFilter.class));
		when(FileFilterUtils.andFileFilter(eq(HiddenFileFilter.VISIBLE), isA(IOFileFilter.class))).thenReturn(resultFilter);
		
		directoryCopier.copyDirectory(srcDir,destDir,".something");
		
		verifyStatic();
		FileUtils.copyDirectory(srcDir, destDir, resultFilter);
	}
	
	
}
