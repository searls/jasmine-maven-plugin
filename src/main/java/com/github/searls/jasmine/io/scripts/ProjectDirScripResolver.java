package com.github.searls.jasmine.io.scripts;

import com.github.searls.jasmine.model.ScriptSearch;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProjectDirScripResolver extends AbstractScriptResolver {

  public ProjectDirScripResolver(File projectBaseDir,
  																ScriptSearch sources,
  																ScriptSearch specs,
  																List<String> preloads,
																	List<String> preloadPatterns) throws IOException {
    super(projectBaseDir,sources,specs,preloads,preloadPatterns);
  }
}
