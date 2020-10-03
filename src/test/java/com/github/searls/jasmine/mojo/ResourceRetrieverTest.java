/*-
 * #%L
 * jasmine-maven-plugin
 * %%
 * Copyright (C) 2010 - 2017 Justin Searls
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.searls.jasmine.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.resource.ResourceManager;
import org.codehaus.plexus.resource.loader.FileResourceCreationException;
import org.codehaus.plexus.resource.loader.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceRetrieverTest {

  @Mock
  private ResourceManager locator;

  @Mock
  private MavenProject mavenProject;

  @InjectMocks
  private ResourceRetriever subject;

  @BeforeEach
  public void beforeEach() {
    File mavenFile = mock(File.class);
    when(mavenProject.getFile()).thenReturn(mavenFile);
    File mavenDir = mock(File.class);
    when(mavenFile.getParentFile()).thenReturn(mavenDir);
    when(mavenDir.getAbsolutePath()).thenReturn("/maven/dir");
  }

  @Test
  public void testGetResource() throws ResourceNotFoundException, MojoExecutionException, FileResourceCreationException {
    File expectedFile = mock(File.class);
    String resourceLocation = "/foo/bar";
    when(locator.getResourceAsFile(resourceLocation)).thenReturn(expectedFile);

    Optional<File> actualFile = subject.getResourceAsFile("param", resourceLocation, mavenProject);

    assertThat(actualFile)
      .isPresent()
      .contains(expectedFile);
  }

  @Test
  public void testResourceNotFound() throws ResourceNotFoundException, FileResourceCreationException {
    String resourceLocation = "/foo/bar";
    when(locator.getResourceAsFile(resourceLocation)).thenThrow(new FileResourceCreationException("Bad Things Happen"));

    assertThatExceptionOfType(MojoExecutionException.class)
      .isThrownBy(() -> subject.getResourceAsFile("param", resourceLocation, mavenProject));
  }
}
