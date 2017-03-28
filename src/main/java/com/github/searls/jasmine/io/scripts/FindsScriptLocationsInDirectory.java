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
import com.github.searls.jasmine.model.ScriptSearch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FindsScriptLocationsInDirectory {

  private final ScansDirectory scansDirectory;
  private final ConvertsFileToUriString convertsFileToUriString;

  public FindsScriptLocationsInDirectory(ScansDirectory scansDirectory,
                                         ConvertsFileToUriString convertsFileToUriString) {
    this.scansDirectory = scansDirectory;
    this.convertsFileToUriString = convertsFileToUriString;
  }

  public FindsScriptLocationsInDirectory() {
    this(new ScansDirectory(), new ConvertsFileToUriString());
  }

  public List<String> find(ScriptSearch search) {
    List<String> scriptLocations = new ArrayList<String>();
    if (search.getDirectory().canRead()) {
      for (String script : scansDirectory.scan(search.getDirectory(), search.getIncludes(), search.getExcludes())) {
        scriptLocations.add(convertsFileToUriString.convert(new File(search.getDirectory(), script)));
      }
    }
    return scriptLocations;
  }
}
