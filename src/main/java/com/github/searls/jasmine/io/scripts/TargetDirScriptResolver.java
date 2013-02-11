package com.github.searls.jasmine.io.scripts;

import com.github.searls.jasmine.AbstractJasmineMojo;
import com.github.searls.jasmine.model.ScriptSearch;

import java.io.File;
import java.io.IOException;

public class TargetDirScriptResolver extends AbstractScriptResolver {


  public TargetDirScriptResolver(AbstractJasmineMojo configuration) throws IOException {
  	super(
  			configuration.getJasmineTargetDir(),
  			searchForDir(new File(configuration.getJasmineTargetDir(), configuration.getSrcDirectoryName()), configuration.getSources()),
  			searchForDir(new File(configuration.getJasmineTargetDir(), configuration.getSpecDirectoryName()), configuration.getSpecs()),
  			configuration.getPreloadSources(),
  			configuration.getPreloadPatterns()
  	);
    resolveScripts();
  }

  private static ScriptSearch searchForDir(File dir, ScriptSearch search) {
    return new ScriptSearch(dir, search.getIncludes(), search.getExcludes());
  }
}
