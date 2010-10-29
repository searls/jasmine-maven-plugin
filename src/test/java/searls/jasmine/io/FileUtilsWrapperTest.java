package searls.jasmine.io;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import searls.jasmine.io.FileUtilsWrapper;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FileUtils.class)
public class FileUtilsWrapperTest {

	private FileUtilsWrapper sut = new FileUtilsWrapper();
	private File file = mock(File.class);

	@Before
	public void powerfullyMockStaticClasses() {
		mockStatic(FileUtils.class);
	}

	@Test
	public void delegatesReadingFilesToFileUtils() throws IOException {
		String expected = "contents";
		when(FileUtils.readFileToString(file)).thenReturn(expected);

		String result = sut.readFileToString(file);

		assertThat(result,is(expected));
	}
	
	@Test
	public void delegatesForcingMkdirToFileUtils() throws IOException {
		sut.forceMkdir(file);

		verifyStatic();
		FileUtils.forceMkdir(file);
	}	
	
	@Test
	public void listFilesDelegatesToFileUtils() {
		Collection<File> expected = new ArrayList<File>();
		String[] extensions = new String[] {"js"};
		boolean recursive = true;
		when(FileUtils.listFiles(file, extensions, recursive)).thenReturn(expected);
		
		Collection<File> files = sut.listFiles(file,extensions,recursive);
		
		assertThat(files,is(sameInstance(expected)));
	}
}