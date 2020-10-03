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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Named
public class IoUtilities {


  public File createFile(File parent, String child) {
    return new File(parent, child);
  }

  public String readFileToString(final File file) throws IOException {
    return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
  }

  public void writeStringToFile(final File file, final String contents) throws IOException {
    FileUtils.writeStringToFile(file, contents, StandardCharsets.UTF_8);
  }

  public void writeStringToFile(final File file, final String contents, String encoding) throws IOException {
    FileUtils.writeStringToFile(file, contents, encoding);
  }

  public String toString(InputStream inputStream) throws IOException {
    return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
  }

  public String resourceToString(String name) throws IOException {
    return toString(resourceToInputStream(name));
  }

  public InputStream resourceToInputStream(String name) {
    return IoUtilities.class.getResourceAsStream(name);
  }
}
