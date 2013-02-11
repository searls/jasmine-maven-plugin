package com.github.searls.jasmine;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.searls.jasmine.model.ScriptSearch;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GenerateManualRunnerMojo.class)
public class GenerateManualRunnerMojoTest {
  private static final String MANUAL_RUNNER_NAME = "runner";
  private static final String SOURCE_DIR = "sauces";
  private static final String SPEC_DIR = "specks";

  @InjectMocks private GenerateManualRunnerMojo subject = new GenerateManualRunnerMojo();

  @Mock private CreatesManualRunner createsManualRunner;

  @Mock private Log log;
  @Mock private ScriptSearch sources;
  @Mock private ScriptSearch specs;
  @Mock private File sourceDirectory;
  @Mock private File specDirectory;


  @Before
  public void fakeOutDirectories() {
    when(sources.getDirectory()).thenReturn(sourceDirectory);
    when(sourceDirectory.getAbsolutePath()).thenReturn(SOURCE_DIR);
    when(sourceDirectory.exists()).thenReturn(true);

    when(specs.getDirectory()).thenReturn(specDirectory);
    when(specDirectory.getAbsolutePath()).thenReturn(SPEC_DIR);
    when(specDirectory.exists()).thenReturn(true);
  }


  @Before
  public void setupMojoProps() {
    subject.sources = sources;
    subject.specs = specs;
    subject.manualSpecRunnerHtmlFileName = MANUAL_RUNNER_NAME;
  }


  @Before
  public void stubNew() throws Exception {
    whenNew(CreatesManualRunner.class).withArguments(subject).thenReturn(createsManualRunner);
  }

  @Before
  public void fakeLog() {
    subject.setLog(log);
  }

  @Test
  public void createsAManualRunner() throws IOException {
    subject.run();

    verify(createsManualRunner).create();
  }

  @Test
  public void whenSpecsOrSourceDoExistLogAboutIt() throws IOException {
    subject.run();

    verify(log).info("Generating runner '"+MANUAL_RUNNER_NAME+"' in the Jasmine plugin's target directory to open in a browser to facilitate faster feedback.");
  }

  @Test
  public void whenSpecsAndSourceDirsDoNotExistLogAboutIt() throws IOException {
    when(specDirectory.exists()).thenReturn(false);
    when(sourceDirectory.exists()).thenReturn(false);

    subject.run();

    verify(log).warn("Skipping manual spec runner generation. Check to make sure that both JavaScript directories `"+SOURCE_DIR+"` and `"+SPEC_DIR+"` exist.");
  }

  @Test
  public void whenSpecsAndSourceDirsDoNotExistDoNotWriteAnyFiles() throws IOException {
    when(specDirectory.exists()).thenReturn(false);
    when(sourceDirectory.exists()).thenReturn(false);

    subject.run();

    verify(createsManualRunner,never()).create();
  }

}
