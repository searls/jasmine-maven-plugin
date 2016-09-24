package com.github.searls.jasmine.mojo;

import com.google.common.base.Optional;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.resource.ResourceManager;
import org.codehaus.plexus.resource.loader.FileResourceCreationException;
import org.codehaus.plexus.resource.loader.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static org.assertj.guava.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResourceRetrieverTest {

  @Mock
  private ResourceManager locator;

  @Mock
  private MavenProject mavenProject;

  @InjectMocks
  private ResourceRetriever subject;

  @Before
  public void setUp() throws Exception {
    File mavenFile = mock(File.class);
    when(mavenProject.getFile()).thenReturn(mavenFile);
    File mavenDir = mock(File.class);
    when(mavenFile.getParentFile()).thenReturn(mavenDir);
    when(mavenDir.getAbsolutePath()).thenReturn("/maven/dir");
  }

  @Test
  public void testGetResource() throws ResourceNotFoundException, MojoExecutionException, MojoFailureException, FileResourceCreationException {
    File expectedFile = mock(File.class);
    String resourceLocation = "/foo/bar";
    when(locator.getResourceAsFile(resourceLocation)).thenReturn(expectedFile);

    Optional<File> actualFile = subject.getResourceAsFile("param", resourceLocation, mavenProject);

    assertThat(actualFile)
      .isPresent()
      .contains(expectedFile);
  }

  @Test(expected = MojoExecutionException.class)
  public void testResourceNotFound() throws ResourceNotFoundException, MojoExecutionException, MojoFailureException, FileResourceCreationException {
    String resourceLocation = "/foo/bar";
    when(locator.getResourceAsFile(resourceLocation)).thenThrow(new FileResourceCreationException("Bad Things Happen"));

    subject.getResourceAsFile("param", resourceLocation, mavenProject);
  }
}
