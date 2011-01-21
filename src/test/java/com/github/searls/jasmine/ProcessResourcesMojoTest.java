package com.github.searls.jasmine;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.searls.jasmine.io.DirectoryCopier;


@RunWith(MockitoJUnitRunner.class)
public class ProcessResourcesMojoTest {

	@InjectMocks private ProcessTestResourcesMojo processTestResourcesMojo = new ProcessTestResourcesMojo();
	@Mock private DirectoryCopier directoryCopier;
	
	@Before
	public void before() {
		//eat logging
		processTestResourcesMojo.setLog(mock(Log.class));
	}
	
	@Test
	public void shouldUseDirectoryCopier() throws IOException, MojoExecutionException, MojoFailureException {
		String expectedSuffix = ".js";
		File srcDir = mock(File.class);
		when(srcDir.exists()).thenReturn(true);
		processTestResourcesMojo.jsTestSrcDir = srcDir;
		processTestResourcesMojo.specDirectoryName = "anything";
		
		processTestResourcesMojo.execute();
		
		verify(directoryCopier).copyDirectory(eq(srcDir), isA(File.class), eq(expectedSuffix));
	}
	
	
	
}
