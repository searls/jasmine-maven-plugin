package com.github.searls.jasmine.io.scripts;

import com.github.searls.jasmine.io.ScansDirectory;
import com.github.searls.jasmine.model.ScriptSearch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FindsScriptLocationsInDirectory {

  private final ScansDirectory scansDirectory;
  private final ConvertsFileToUriString convertsFileToUriString;

  public FindsScriptLocationsInDirectory(ScansDirectory scansDirectory,
                                         ConvertsFileToUriString convertsFileToUriString) {
    this.scansDirectory = scansDirectory;
    this.convertsFileToUriString = convertsFileToUriString;
  }

  public FindsScriptLocationsInDirectory() {
    this(new ScansDirectory(), new ConvertsFileToUriString());
  }

  public List<String> find(ScriptSearch search) {
    List<String> scriptLocations = new ArrayList<String>();
    if (search.getDirectory().canRead()) {
      for (String script : scansDirectory.scan(search.getDirectory(), search.getIncludes(), search.getExcludes())) {
        scriptLocations.add(convertsFileToUriString.convert(new File(search.getDirectory(), script)));
      }
    }
    return scriptLocations;
  }
}
