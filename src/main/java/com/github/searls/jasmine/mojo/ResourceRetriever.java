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

import com.google.common.base.Optional;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.resource.ResourceManager;
import org.codehaus.plexus.resource.loader.FileResourceLoader;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;

@Named
public class ResourceRetriever {

  private static final String ERROR_FILE_DNE = "Invalid value for parameter '%s'. File does not exist: %s";

  private final ResourceManager locator;

  @Inject
  public ResourceRetriever(ResourceManager locator) {
    this.locator = locator;
  }

  public Optional<File> getResourceAsFile(final String parameter, final String resourceLocation, final MavenProject mavenProject) throws MojoExecutionException {
    File file = null;

    if (resourceLocation != null) {
      locator.addSearchPath("url", "");
      locator.addSearchPath(FileResourceLoader.ID, mavenProject.getFile().getParentFile().getAbsolutePath());

      ClassLoader origLoader = Thread.currentThread().getContextClassLoader();
      try {
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        try {
          file = locator.getResourceAsFile(resourceLocation);
        } catch (Exception e) {
          throw new MojoExecutionException(String.format(ERROR_FILE_DNE, parameter, resourceLocation));
        }
      } finally {
        Thread.currentThread().setContextClassLoader(origLoader);
      }
    }
    return Optional.fromNullable(file);
  }
}
