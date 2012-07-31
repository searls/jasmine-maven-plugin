package com.github.searls.jasmine.model;

import java.io.File;
import java.util.List;

public class ScriptSearch {

  private File directory;
  private List<String> includes;
  private List<String> excludes;

  public ScriptSearch(File directory, List<String> includes, List<String> excludes) {
    this.directory = directory;
    this.includes = includes;
    this.excludes = excludes;
  }

  public File getDirectory() {
    return directory;
  }

  public List<String> getIncludes() {
    return includes;
  }

  public List<String> getExcludes() {
    return excludes;
  }

}
