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
package com.github.searls.jasmine.config;

import com.github.searls.jasmine.model.FileSystemReporter;
import com.github.searls.jasmine.model.Reporter;
import com.github.searls.jasmine.model.ScriptSearch;
import com.github.searls.jasmine.mojo.Context;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.runner.SpecRunnerTemplate;
import com.google.common.base.Optional;
import org.immutables.value.Value;

import java.io.File;
import java.util.List;

@Value.Immutable
public interface JasmineConfiguration {

  File getBasedir();

  File getJasmineTargetDir();

  String getSrcDirectoryName();

  String getSpecDirectoryName();

  ScriptSearch getSources();

  ScriptSearch getSpecs();

  List<Context> getContexts();

  List<String> getPreloadSources();

  String getSourceEncoding();

  SpecRunnerTemplate getSpecRunnerTemplate();

  Optional<File> getCustomRunnerTemplate();

  Optional<File> getCustomRunnerConfiguration();

  String getSpecRunnerHtmlFileName();

  ReporterType getReporterType();

  List<Reporter> getReporters();

  List<FileSystemReporter> getFileSystemReporters();

  int getAutoRefreshInterval();

  ClassLoader getProjectClassLoader();
}
