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
import org.mockito.Mockito;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.when;

public class ProjectClassLoaderHelper {

  static public ClassLoader projectClassLoaderOf(String jarPath) {
    Artifact artifact = Mockito.mock(Artifact.class);
    Set<Artifact> artifacts = new HashSet<Artifact>();
    artifacts.add(artifact);
    File jarFile = new File(jarPath);
    when(artifact.getFile()).thenReturn(jarFile);
    return new ProjectClassLoaderFactory(artifacts).create();
  }
}
