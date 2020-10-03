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

import com.github.searls.jasmine.io.ScansDirectory;
import com.github.searls.jasmine.model.ImmutableScriptSearch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FindsScriptLocationsInDirectoryTest {

  private static final List<String> INCLUDES = Collections.singletonList("So in");
  private static final List<String> EXCLUDES = Collections.singletonList("So out");
  private static final String FILE_LOCATION = "blah/a.js";

  @Mock
  private ScansDirectory scansDirectory;

  @Mock
  private ConvertsFileToUriString convertsFileToUriString;

  @Spy
  private File directory = new File("Not quite a real directory");

  @InjectMocks
  private FindsScriptLocationsInDirectory subject;

  @Test
  public void returnsEmptyWhenDirectoryDoesNotExist() {
    List<String> result = subject.find(
      ImmutableScriptSearch.builder().directory(new File("No way does this file exist")).build()
    );

    assertThat(result).isEmpty();
  }

  @Test
  public void addsScriptLocationScannerFinds() {
    String expected = "full blown file";
    when(directory.canRead()).thenReturn(true);
    when(scansDirectory.scan(directory, INCLUDES, EXCLUDES)).thenReturn(Collections.singletonList(FILE_LOCATION));
    when(convertsFileToUriString.convert(new File(directory, FILE_LOCATION))).thenReturn(expected);

    List<String> result = subject.find(
      ImmutableScriptSearch.builder().directory(directory).includes(INCLUDES).excludes(EXCLUDES).build()
    );

    assertThat(result).contains(expected);
  }

}
