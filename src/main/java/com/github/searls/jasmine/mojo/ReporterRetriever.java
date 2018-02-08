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
package com.github.searls.jasmine.mojo;

import com.github.searls.jasmine.model.FileSystemReporter;
import com.github.searls.jasmine.model.Reporter;
import com.github.searls.jasmine.model.Reporters;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Named
public class ReporterRetriever {

  protected static final String STANDARD_REPORTER = "/lib/buildReport.js";
  protected static final String JUNIT_XML_REPORTER = "lib/createJunitXml.js";

  private final ResourceRetriever resourceRetriever;

  @Inject
  public ReporterRetriever(ResourceRetriever resourceRetriever) {
    this.resourceRetriever = resourceRetriever;
  }

  List<FileSystemReporter> retrieveFileSystemReporters(final List<FileSystemReporter> reporters,
                                                       final File targetDirectory,
                                                       final MavenProject mavenProject) throws MojoExecutionException {
    List<FileSystemReporter> normalizedReporters = new ArrayList<>();

    if (reporters.isEmpty()) {
      reporters.add(Reporters.JUNIT_REPORTER);
    }

    for (FileSystemReporter reporter : reporters) {
      String name = reporter.getReporterName();
      if (Reporters.JUNIT_REPORTER.getReporterName().equals(reporter.getReporterName())) {
        name = JUNIT_XML_REPORTER;
      }
      getReporterFile(name, mavenProject).ifPresent(file1 -> {
        File file = new File(targetDirectory, reporter.getFileName());
        normalizedReporters.add(new FileSystemReporter(file, file1));
      });
    }

    return normalizedReporters;
  }

  List<Reporter> retrieveReporters(final List<Reporter> reporters,
                                   final MavenProject mavenProject) throws MojoExecutionException {
    List<Reporter> normalizedReporters = new ArrayList<>();

    if (reporters.isEmpty()) {
      reporters.add(Reporters.STANDARD_REPORTER);
    }
    for (Reporter reporter : reporters) {
      String name = reporter.getReporterName();
      if (Reporters.STANDARD_REPORTER.getReporterName().equals(name)) {
        name = STANDARD_REPORTER;
      }
      Optional<File> reporterFile = getReporterFile(name, mavenProject);
      if (reporterFile.isPresent()) {
        normalizedReporters.add(new Reporter(name, reporterFile.get()));
      }
    }

    return normalizedReporters;
  }

  private Optional<File> getReporterFile(final String reporter, final MavenProject mavenProject) throws MojoExecutionException {
    return resourceRetriever.getResourceAsFile("reporter", reporter, mavenProject);
  }
}
