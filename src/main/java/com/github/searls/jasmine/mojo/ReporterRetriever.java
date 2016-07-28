package com.github.searls.jasmine.mojo;

import com.github.searls.jasmine.model.FileSystemReporter;
import com.github.searls.jasmine.model.Reporter;
import com.google.inject.Inject;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;

public class ReporterRetriever {
  public static final String STANDARD_REPORTER = "/lib/buildReport.js";
  private static final String STANDARD_REPORTER_KEY = "STANDARD";

  private static final String JUNIT_XML_FILENAME = "TEST-jasmine.xml";
  private static final String JUNIT_XML_KEY = "JUNIT_XML";
  public static final String JUNIT_XML_REPORTER = "lib/createJunitXml.js";

  private final ResourceRetriever resourceRetriever;

  @Inject
  public ReporterRetriever(final ResourceRetriever resourceRetriever) {
    this.resourceRetriever = resourceRetriever;
  }

  List<FileSystemReporter> retrieveFileSystemReporters(final List<FileSystemReporter> reporters, final File targetDirectory, final MavenProject mavenProject) throws MojoExecutionException {
    if (reporters.isEmpty()) {
      reporters.add(new FileSystemReporter(JUNIT_XML_FILENAME, JUNIT_XML_KEY));
    }

    for (FileSystemReporter reporter : reporters) {
      if (JUNIT_XML_KEY.equals(reporter.reporterName)) {
        reporter.reporterName = JUNIT_XML_REPORTER;
      }
      reporter.reporterFile = getReporter(reporter.reporterName, mavenProject);
      reporter.file = new File(targetDirectory, reporter.fileName);
    }

    return reporters;
  }

  List<Reporter> retrieveReporters(final List<Reporter> reporters, final MavenProject mavenProject) throws MojoExecutionException {
    if (reporters.isEmpty()) {
      reporters.add(new Reporter(STANDARD_REPORTER_KEY));
    }

    for (Reporter reporter : reporters) {
      if (STANDARD_REPORTER_KEY.equals(reporter.reporterName)) {
        reporter.reporterName = STANDARD_REPORTER;
      }
      reporter.reporterFile = getReporter(reporter.reporterName, mavenProject);
    }

    return reporters;
  }

  private File getReporter(final String reporter, final MavenProject mavenProject) throws MojoExecutionException {
    return resourceRetriever.getResourceAsFile("reporter", reporter, mavenProject);
  }
}
