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
package com.github.searls.jasmine.io;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class RelativizesFilePathsTest {

  private RelativizesFilePaths subject = new RelativizesFilePaths();

  private File from;
  private File to;

  @Test
  public void resolvesWithinDir() throws IOException {
    from = new File("/panda/");
    to = new File("/panda/pants.txt");

    String result = subject.relativize(from, to);

    assertThat(result).isEqualTo("pants.txt");
  }

  @Test
  public void resolvesSubDir() throws IOException {
    from = new File("/panda/");
    to = new File("/panda/target/jasmine/");

    String result = subject.relativize(from, to);

    assertThat(result).isEqualTo("target/jasmine");
  }


  @Test
  public void resolvesDeepChild() throws IOException {
    from = new File(slash("/Volumes/blah/Users/justin/code/workspaces/jasmine_maven/jasmine-maven-plugin/src/test/resources/examples/jasmine-webapp-coffee/target/jasmine"));
    to = new File(slash("/Volumes/blah/Users/justin/code/workspaces/jasmine_maven/jasmine-maven-plugin/src/test/resources/examples/jasmine-webapp-coffee/src/test/javascript/fun-spec.js"));

    String result = subject.relativize(from, to);

    assertThat(result).isEqualTo("../../src/test/javascript/fun-spec.js");
  }

  private String slash(String s) {
    return s.replace('/', File.separatorChar);
  }

}
