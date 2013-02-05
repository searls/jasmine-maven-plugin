package com.github.searls.jasmine.io.scripts;

import com.github.searls.jasmine.AbstractJasmineMojo;
import com.github.searls.jasmine.io.ScansDirectory;
import com.github.searls.jasmine.model.ScriptSearch;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;

public class TargetDirScriptResolver extends AbstractScriptResolver {


  public TargetDirScriptResolver(final AbstractJasmineMojo configuration) throws IOException {
    this.baseDir = configuration.getJasmineTargetDir();
    this.preloads = configuration.getPreloadSources();
    this.scriptSearchSources = searchForDir(new File(this.baseDir, configuration.getSrcDirectoryName()), configuration.getSources());
    this.scriptSearchSpecs = searchForDir(new File(this.baseDir, configuration.getSpecDirectoryName()), configuration.getSpecs());

    resolveScripts();
  }

  private ScriptSearch searchForDir(final File dir, final ScriptSearch search) {
    copySources(dir, search);
    return new ScriptSearch(dir, search.getIncludes(), search.getExcludes());
  }

  /** Copies a set of source files to the specified target directory.
   *
   * <p>
   * This is needed because spec runners read files from the target directory.
   * Fix for issue #96.
   * </p>
   * @param target Jasmine target directory for copy files to. Cannot be null.
   * @param search Search containing the set of files to copy. Cannot be null.
   */
  private void copySources(final File target, final ScriptSearch search) {
    Validate.notNull(target, "The target directory cannot be null.");
    Validate.notNull(search, "The search definition cannot be null.");

    // Creates target directory if it doesn't exist.
    if (!target.exists()) {
      target.mkdirs();
    }

    ScansDirectory scanner = new ScansDirectory();
    final List<String> files = scanner.scan(search.getDirectory(),
        search.getIncludes(), search.getExcludes());

    for (String file : files) {
      try {
        FileUtils.copyFile(new File(search.getDirectory(), file),
            new File(target, file));
      } catch (IOException cause) {
        throw new RuntimeException("Cannot copy source files.", cause);
      }
    }
  }
}
