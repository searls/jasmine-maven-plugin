package com.github.searls.jasmine;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.searls.jasmine.coffee.CompilesAllCoffeeInDirectory;
import com.github.searls.jasmine.io.DirectoryCopier;
import com.github.searls.jasmine.model.ScriptSearch;


@RunWith(PowerMockRunner.class)
@PrepareForTest(ProcessTestResourcesMojo.class)
public class ProcessTestResourcesMojoTest {

private static final String SPEC_DIR_NAME = "blarh";

  @InjectMocks private ProcessTestResourcesMojo subject = new ProcessTestResourcesMojo();

  @Mock private DirectoryCopier directoryCopier;
  @Mock private CompilesAllCoffeeInDirectory compilesAllCoffeeInDirectory;

  @Mock private File jasmineTargetDir;

  @Mock private File specDir;
  @Mock private File targetDir;
  @Mock private ScriptSearch specs;

  @Mock private Log log;

  @Before
  public void killLogging() {
    subject.setLog(log);
  }

  @Before
  public void isolateSubject() throws Exception {
    subject.jasmineTargetDir = jasmineTargetDir;
    subject.specDirectoryName = SPEC_DIR_NAME;
    subject.specs = specs;

    whenNew(File.class).withArguments(jasmineTargetDir,SPEC_DIR_NAME).thenReturn(targetDir);
  }

  @Before
  public void stubSources() {
    when(specs.getDirectory()).thenReturn(specDir);
  }

  @Test
  public void basicLogging() throws IOException {
    subject.run();

    verify(log).info("Processing JavaScript Specs");
  }

  @Test
  public void whenDirectoryExistsCopy() throws IOException, MojoExecutionException, MojoFailureException {
    when(specDir.exists()).thenReturn(true);

    subject.run();

    verify(directoryCopier).copyDirectory(specs.getDirectory(), targetDir);
  }

  @Test
  public void whenDirectoryExistsCoffeeTime() throws IOException {
    when(specDir.exists()).thenReturn(true);

    subject.run();

    verify(compilesAllCoffeeInDirectory).compile(targetDir);
  }

  @Test
  public void whenDirectoryDoesNotExistDoNotDoStuff() throws IOException, MojoExecutionException, MojoFailureException {
    when(specs.getDirectory()).thenReturn(specDir);
    when(specDir.exists()).thenReturn(false);

    subject.run();

    verify(directoryCopier,never()).copyDirectory(any(File.class),any(File.class));
    verify(compilesAllCoffeeInDirectory,never()).compile(any(File.class));
    verify(log).warn(ProcessTestResourcesMojo.MISSING_DIR_WARNING);
  }


}
