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
package com.github.searls.jasmine.thirdpartylibs;

import org.apache.maven.artifact.Artifact;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProjectClassLoaderFactory {

  private final Set<Artifact> artifacts;

  public ProjectClassLoaderFactory() {
    this(new HashSet<Artifact>());
  }

  public ProjectClassLoaderFactory(Set<Artifact> artifacts) {
    this.artifacts = artifacts;
  }

  public ClassLoader create() {
    final List<String> classpathElements = new ArrayList<String>();
    for (Artifact artifact : artifacts) {
      classpathElements.add(artifact.getFile().getAbsolutePath());
    }
    return createURLClassLoader(classpathElements);
  }

  private ClassLoader createURLClassLoader(final List<String> classpathElements) {
    final List<URL> urls = new ArrayList<URL>();
    try {
      for (final String element : classpathElements) {
        final File elementFile = new File(element);
        urls.add(elementFile.toURI().toURL());
      }
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
    return new URLClassLoader(urls.toArray(new URL[urls.size()]), Thread.currentThread().getContextClassLoader());
  }
}
