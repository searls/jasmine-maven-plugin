package com.github.searls.jasmine.mojo;

import com.google.common.base.Optional;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.resource.ResourceManager;
import org.codehaus.plexus.resource.loader.FileResourceLoader;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;

@Named
public class ResourceRetriever {

  private static final String ERROR_FILE_DNE = "Invalid value for parameter '%s'. File does not exist: %s";

  private final ResourceManager locator;

  @Inject
  public ResourceRetriever(ResourceManager locator) {
    this.locator = locator;
  }

  public Optional<File> getResourceAsFile(final String parameter, final String resourceLocation, final MavenProject mavenProject) throws MojoExecutionException {
    File file = null;

    if (resourceLocation != null) {
      locator.addSearchPath("url", "");
      locator.addSearchPath(FileResourceLoader.ID, mavenProject.getFile().getParentFile().getAbsolutePath());

      ClassLoader origLoader = Thread.currentThread().getContextClassLoader();
      try {
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        try {
          file = locator.getResourceAsFile(resourceLocation);
        } catch (Exception e) {
          throw new MojoExecutionException(String.format(ERROR_FILE_DNE, parameter, resourceLocation));
        }
      } finally {
        Thread.currentThread().setContextClassLoader(origLoader);
      }
    }
    return Optional.fromNullable(file);
  }
}
