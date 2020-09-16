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

import com.github.searls.jasmine.config.ImmutableServerConfiguration;
import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.config.ServerConfiguration;
import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.model.ScriptSearch;
import com.github.searls.jasmine.server.ResourceHandlerConfigurator;
import com.github.searls.jasmine.server.ServerManager;
import com.github.searls.jasmine.server.ServerManagerFactory;
import org.apache.maven.project.MavenProject;
import org.eclipse.jetty.server.Handler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServerMojoTest {

  private static final int PORT = 8923;

  @Mock
  private MavenProject mavenProject;

  @Mock
  private JasmineConfiguration jasmineConfiguration;

  @Mock
  private RelativizesFilePaths relativizesFilePaths;

  @Mock
  private File sourceDir;

  @Mock
  private File specDir;

  @Mock
  private ScriptSearch sources;

  @Mock
  private ScriptSearch specs;

  @Mock
  private ResourceHandlerConfigurator configurator;

  @Mock
  private ServerManager serverManager;

  @Mock
  private ServerManagerFactory serverManagerFactory;

  @Mock
  private Handler handler;

  @InjectMocks
  private ServerMojo subject;

  @Test
  public void testRunMojo() throws Exception {

    when(jasmineConfiguration.getSources()).thenReturn(sources);
    when(jasmineConfiguration.getSpecs()).thenReturn(specs);

    when(sources.getDirectory()).thenReturn(sourceDir);
    when(specs.getDirectory()).thenReturn(specDir);
    when(serverManagerFactory.create()).thenReturn(serverManager);
    when(configurator.createHandler(jasmineConfiguration)).thenReturn(handler);
    when(serverManager.start(PORT, handler)).thenReturn(PORT);

    ServerConfiguration serverConfiguration = ImmutableServerConfiguration.builder()
      .uriScheme("http")
      .serverHostname("localhost")
      .serverPort(PORT)
      .build();

    subject.run(serverConfiguration, jasmineConfiguration);

    verify(serverManager).start(PORT, handler);
    verify(serverManager).join();
  }
}
