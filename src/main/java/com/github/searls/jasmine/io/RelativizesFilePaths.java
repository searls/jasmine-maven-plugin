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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import javax.inject.Named;
import java.io.File;
import java.io.IOException;

@Named
public class RelativizesFilePaths {

  public String relativize(File from, File to) throws IOException {
    String fromPath = from.getCanonicalPath();
    String toPath = to.getCanonicalPath();

    String root = StringUtils.getCommonPrefix(fromPath, toPath);
    StringBuffer result = new StringBuffer();
    if (this.fromPathIsNotADirectAncestor(fromPath, root)) {
      for (@SuppressWarnings("unused") String dir : this.divergentDirectories(root, fromPath)) {
        result.append("..").append(File.separator);
      }
    }
    result.append(this.pathAfterRoot(toPath, root));

    return this.convertSlashes(this.trimLeadingSlashIfNecessary(result));
  }

  private String convertSlashes(String path) {
    return path.replace(File.separatorChar, '/');
  }

  private boolean fromPathIsNotADirectAncestor(String fromPath, String root) {
    return !StringUtils.equals(root, fromPath);
  }

  private String[] divergentDirectories(String root, String fullPath) {
    return this.pathAfterRoot(fullPath, root).split(StringEscapeUtils.escapeJava(File.separator));
  }

  private String pathAfterRoot(String path, String root) {
    return StringUtils.substringAfterLast(path, root);
  }

  private String trimLeadingSlashIfNecessary(StringBuffer result) {
    return StringUtils.removeStart(result.toString(), File.separator);
  }

}
