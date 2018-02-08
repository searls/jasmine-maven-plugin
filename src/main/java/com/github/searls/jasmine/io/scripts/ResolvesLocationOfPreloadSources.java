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

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Named
public class ResolvesLocationOfPreloadSources {

  private final ConvertsFileToUriString convertsFileToUriString;

  @Inject
  public ResolvesLocationOfPreloadSources(ConvertsFileToUriString convertsFileToUriString) {

    this.convertsFileToUriString = convertsFileToUriString;
  }

  public List<String> resolve(List<String> preloadSources, File sourceDir, File specDir) {
    List<String> sources = new ArrayList<>();
    if (preloadSources != null) {
      for (String source : preloadSources) {
        File sourceFile = getAsFile(sourceDir, specDir, source);
        if (sourceFile.exists()) {
          sources.add(convertsFileToUriString.convert(sourceFile));
        } else {
          sources.add(source);
        }
      }
    }
    return sources;
  }

  private File getAsFile(File sourceDir, File specDir, String source) {
    File sourceFile = new File(sourceDir, source);

    if (!sourceFile.exists()) {
      sourceFile = new File(specDir, source);
    }

    if (!sourceFile.exists()) {
      sourceFile = new File(source);
    }

    return sourceFile;
  }
}
