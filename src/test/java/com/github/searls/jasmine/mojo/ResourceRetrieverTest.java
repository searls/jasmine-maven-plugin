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
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.resource.ResourceManager;
import org.codehaus.plexus.resource.loader.FileResourceCreationException;
import org.codehaus.plexus.resource.loader.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResourceRetrieverTest {

  @Mock
  ResourceManager locator;

  @Mock
  MavenProject mavenProject;

  ResourceRetriever subject;

  @Before
  public void setUp() throws Exception {
    subject = new ResourceRetriever(locator);
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

    File actualFile = subject.getResourceAsFile("param", resourceLocation, mavenProject);

    assertThat(actualFile, is(expectedFile));
  }

  @Test(expected = MojoExecutionException.class)
  public void testResourceNotFound() throws ResourceNotFoundException, MojoExecutionException, MojoFailureException, FileResourceCreationException {
    String resourceLocation = "/foo/bar";
    when(locator.getResourceAsFile(resourceLocation)).thenThrow(new FileResourceCreationException("Bad Things Happen"));

    subject.getResourceAsFile("param", resourceLocation, mavenProject);
  }
}
