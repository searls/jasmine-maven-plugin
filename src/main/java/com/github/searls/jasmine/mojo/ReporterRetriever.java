package com.github.searls.jasmine.mojo;

import com.google.inject.Inject;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReporterRetriever {
  static final String STANDARD_REPORTER = "/lib/buildReport.js";
  private static final String STANDARD_REPORTER_KEY = "STANDARD";

  private final ResourceRetriever resourceRetriever;

  @Inject
  public ReporterRetriever(final ResourceRetriever resourceRetriever) {
    this.resourceRetriever = resourceRetriever;
  }

  List<File> retrieveReporters(final List<String> reporters, final MavenProject mavenProject) throws MojoExecutionException {
    final List<File> reporterFiles = new ArrayList<File>();
    for (String reporter : reporters) {
      if (STANDARD_REPORTER_KEY.equals(reporter)) {
        reporterFiles.add(getStandardReporter(mavenProject));
      } else {
        reporterFiles.add(getReporter(mavenProject, reporter));
      }
    }

    if (reporterFiles.isEmpty()) {
      reporterFiles.add(getStandardReporter(mavenProject));
    }

    return reporterFiles;
  }

  private File getStandardReporter(final MavenProject mavenProject) throws MojoExecutionException {
    return getReporter(mavenProject, STANDARD_REPORTER);
  }

  private File getReporter(final MavenProject mavenProject, final String reporter) throws MojoExecutionException {
    return resourceRetriever.getResourceAsFile("reporter", reporter, mavenProject);
  }
}
