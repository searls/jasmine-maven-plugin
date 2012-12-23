package com.github.searls.jasmine;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.whenNew;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.searls.jasmine.coffee.CompilesAllCoffeeInDirectory;
import com.github.searls.jasmine.io.DirectoryCopier;
import com.github.searls.jasmine.model.OverlayScriptSearch;
import com.github.searls.jasmine.model.ScriptSearch;


@RunWith(PowerMockRunner.class)
@PrepareForTest(ProcessResourcesMojo.class)
public class ProcessResourcesMojoTest {

  private static final String SRC_DIR_NAME = "blarh";
  private static final String OVERLAY_DIR_NAME = "overlaydir";

  @InjectMocks private ProcessResourcesMojo subject = new ProcessResourcesMojo();

  @Mock private DirectoryCopier directoryCopier;
  @Mock private CompilesAllCoffeeInDirectory compilesAllCoffeeInDirectory;

  @Mock private File jasmineTargetDir;

  @Mock private File sourceDir;
  @Mock private File targetDir;
  @Mock private File overlayDir;
  @Mock private ScriptSearch sources;
  @Mock private OverlayScriptSearch overlayScriptSearch;

  @Mock private Log log;

  private List<OverlayScriptSearch> warOverlays;
  
  @Before
  public void killLogging() {
    subject.setLog(log);
  }

  @Before
  public void isolateSubject() throws Exception {
    subject.jasmineTargetDir = jasmineTargetDir;
    subject.srcDirectoryName = SRC_DIR_NAME;
    subject.sources = sources;    
    subject.warOverlays = warOverlays;

    whenNew(File.class).withArguments(jasmineTargetDir,SRC_DIR_NAME).thenReturn(targetDir);
    whenNew(File.class).withArguments(jasmineTargetDir, OVERLAY_DIR_NAME).thenReturn(targetDir);
  }

  @Before
  public void stubSources() {
    when(sources.getDirectory()).thenReturn(sourceDir);
  }
  
  @Before
  public void stubWarOverlays() {
	warOverlays = new ArrayList<OverlayScriptSearch>();
	warOverlays.add(overlayScriptSearch);
	when(overlayScriptSearch.getDirectory()).thenReturn(overlayDir);
	when(overlayScriptSearch.getSrcDirectoryName()).thenReturn(OVERLAY_DIR_NAME);
  }

  @Test
  public void basicLogging() throws IOException {
    subject.run();

    verify(log).info("Processing JavaScript Sources");
  }

  @Test
  public void whenDirectoryExistsCopy() throws IOException, MojoExecutionException, MojoFailureException {
    when(sourceDir.exists()).thenReturn(true);
    when(overlayDir.exists()).thenReturn(true);

    subject.run();

    verify(directoryCopier).copyDirectory(sources.getDirectory(), targetDir);
    verify(directoryCopier).copyDirectory(overlayScriptSearch.getDirectory(), targetDir);
  }

  @Test
  public void whenDirectoryExistsCoffeeTime() throws IOException {
    when(sourceDir.exists()).thenReturn(true);

    subject.run();

    verify(compilesAllCoffeeInDirectory).compile(targetDir);
  }

  @Test
  public void whenDirectoryDoesNotExistDoNotDoStuff() throws IOException, MojoExecutionException, MojoFailureException {
    when(sources.getDirectory()).thenReturn(sourceDir);
    when(sourceDir.exists()).thenReturn(false);

    subject.run();

    verify(directoryCopier,never()).copyDirectory(any(File.class),any(File.class));
    verify(compilesAllCoffeeInDirectory,never()).compile(any(File.class));
    verify(log).warn(ProcessResourcesMojo.MISSING_DIR_WARNING);
  }
  
  @Test
  public void whenOverlayDirectoryDoesNotExistDoNotCopy() throws IOException, MojoExecutionException, MojoFailureException {
	  when(sourceDir.exists()).thenReturn(true);
	  when(overlayDir.exists()).thenReturn(false);
	  
	    subject.run();

	    verify(directoryCopier).copyDirectory(sources.getDirectory(), targetDir);
	    verify(directoryCopier, never()).copyDirectory(overlayScriptSearch.getDirectory(), targetDir);	  
  }

}
