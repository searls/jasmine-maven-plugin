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
import com.github.searls.jasmine.model.Reporters;
import com.github.searls.jasmine.model.ScriptSearch;
import com.github.searls.jasmine.mojo.Context;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.runner.SpecRunnerTemplate;
import org.immutables.value.Value;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Value.Immutable
@Value.Style(jdkOnly = true)
public abstract class JasmineConfiguration {

  public abstract File getBasedir();

  public abstract File getJasmineTargetDir();

  @Value.Default
  public String getSrcDirectoryName(){
    return "src";
  }

  @Value.Default
  public String getSpecDirectoryName() {
    return "spec";
  }

  public abstract ScriptSearch getSources();

  public abstract ScriptSearch getSpecs();

  public abstract List<Context> getContexts();

  public abstract List<String> getPreloadSources();

  @Value.Default
  public String getSourceEncoding() {
    return StandardCharsets.UTF_8.name();
  }

  @Value.Default
  public SpecRunnerTemplate getSpecRunnerTemplate() {
    return SpecRunnerTemplate.DEFAULT;
  }

  public abstract Optional<File> getCustomRunnerTemplate();

  public abstract Optional<File> getCustomRunnerConfiguration();

  @Value.Default
  public String getSpecRunnerHtmlFileName() {
    return "SpecRunner.html";
  }

  @Value.Default
  public ReporterType getReporterType() {
    return ReporterType.HtmlReporter;
  }

  @Value.Default
  public List<Reporter> getReporters() {
    return Collections.singletonList(Reporters.STANDARD_REPORTER);
  }

  @Value.Default
  public List<FileSystemReporter> getFileSystemReporters() {
    return Collections.singletonList(Reporters.JUNIT_REPORTER);
  }

  @Value.Default
  public int getAutoRefreshInterval() {
    return 0;
  }

  @Value.Default
  public ClassLoader getProjectClassLoader() {
    return this.getClass().getClassLoader();
  }
}
