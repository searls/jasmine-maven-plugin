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

import com.github.searls.jasmine.collections.CollectionHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ResolvesLocationOfPreloadSources {

  private CollectionHelper collectionHelper = new CollectionHelper();
  private ConvertsFileToUriString convertsFileToUriString = new ConvertsFileToUriString();

  public List<String> resolve(List<String> preloadSources, File sourceDir, File specDir) {
    List<String> sources = new ArrayList<String>();
    for (String source : collectionHelper.list(preloadSources)) {
      if (fileCouldNotBeAdded(new File(sourceDir, source), sources)
        && fileCouldNotBeAdded(new File(specDir, source), sources)
        && fileCouldNotBeAdded(new File(source), sources)) {
        sources.add(source);
      }
    }
    return sources;
  }

  private boolean fileCouldNotBeAdded(File file, List<String> sourcePaths) {
    boolean canAdd = file.exists();
    if (canAdd) {
      sourcePaths.add(convertsFileToUriString.convert(file));
    }
    return !canAdd;
  }

}
