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
package com.github.searls.jasmine.io.scripts;

import com.github.searls.jasmine.model.ScriptSearch;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BasicScriptResolverTest {

  private static final String SOURCE_DIRECTORY = "/project/source/directory";
  private static final String SPEC_DIRECTORY = "/project/spec/directory";

  @Mock
  private ResolvesLocationOfPreloadSources resolvesPreloadSources;

  @Mock
  private FindsScriptLocationsInDirectory findsScriptLocations;

  @Mock
  private ScriptSearch sourceScriptSearch;

  @Mock
  private ScriptSearch specScriptSearch;

  @Mock
  private File baseDirectory;

  @Mock
  private File sourceDirectory;

  @Mock
  private File specDirectory;

  private URI sourceURI;

  private URI specURI;

  private List<String> preloadList;

  private BasicScriptResolver resolver;

  @Before
  public void before() throws URISyntaxException {
    this.sourceURI = new URI(SOURCE_DIRECTORY);
    this.specURI = new URI(SPEC_DIRECTORY);
    this.preloadList = new ArrayList<String>();
  }

  @Test
  public void testGetSourceDirectory() {
    when(sourceDirectory.toURI()).thenReturn(this.sourceURI);

    when(this.sourceScriptSearch.getDirectory()).thenReturn(sourceDirectory);
    when(this.specScriptSearch.getDirectory()).thenReturn(specDirectory);

    this.resolver = new BasicScriptResolver(
      resolvesPreloadSources,
      findsScriptLocations,
      baseDirectory,
      sourceScriptSearch,
      specScriptSearch,
      preloadList
    );

    assertThat(this.resolver.getSourceDirectory()).isEqualTo(SOURCE_DIRECTORY);
  }

  @Test
  public void testGetSpecDirectory() {
    when(specDirectory.toURI()).thenReturn(this.specURI);

    when(this.sourceScriptSearch.getDirectory()).thenReturn(sourceDirectory);
    when(this.specScriptSearch.getDirectory()).thenReturn(specDirectory);

    this.resolver = new BasicScriptResolver(
      resolvesPreloadSources,
      findsScriptLocations,
      baseDirectory,
      sourceScriptSearch,
      specScriptSearch,
      preloadList
    );

    assertThat(this.resolver.getSpecDirectory()).isEqualTo(SPEC_DIRECTORY);
  }
}
