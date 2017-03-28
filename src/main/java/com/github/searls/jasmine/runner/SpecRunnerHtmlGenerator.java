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
package com.github.searls.jasmine.runner;

public interface SpecRunnerHtmlGenerator {
  String DEFAULT_SOURCE_ENCODING = "UTF-8";
  String JASMINE_JS = "/webjars/jasmine/jasmine.js";
  String JASMINE_HTML_JS = "/webjars/jasmine/jasmine-html.js";
  String JASMINE_BOOT_JS = "/webjars/jasmine/boot.js";
  String JASMINE_CSS = "/webjars/jasmine/jasmine.css";
  String JASMINE_HTMLSPECFILTER_PATCH_JS = "/classpath/lib/htmlSpecFilterPatch.js";

  String generate();
}
