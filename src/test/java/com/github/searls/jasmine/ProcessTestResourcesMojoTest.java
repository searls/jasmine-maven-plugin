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
import com.github.searls.jasmine.model.ScriptSearch;


@RunWith(MockitoJUnitRunner.class)
public class ProcessTestResourcesMojoTest {

	@InjectMocks private ProcessResourcesMojo subject = new ProcessResourcesMojo();
	@Mock private DirectoryCopier directoryCopier;
	
	@Before
	public void killLogging() {
		subject.setLog(mock(Log.class));
	}
	
	@Test
	public void shouldUseDirectoryCopier() throws IOException, MojoExecutionException, MojoFailureException {
		File srcDir = mock(File.class);
		when(srcDir.exists()).thenReturn(true);
		subject.sources = new ScriptSearch(srcDir,null,null);
		subject.srcDirectoryName = "blah";
		
		subject.run();
		
		verify(directoryCopier).copyDirectory(eq(srcDir), isA(File.class));
	}
	
	
	
}
