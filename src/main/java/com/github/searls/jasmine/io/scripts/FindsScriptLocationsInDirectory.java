package com.github.searls.jasmine.io.scripts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.searls.jasmine.io.ScansDirectory;
import com.github.searls.jasmine.model.ScriptSearch;

public class FindsScriptLocationsInDirectory {

  private final ScansDirectory scansDirectory = new ScansDirectory();
  private final ConvertsFileToUriString convertsFileToUriString = new ConvertsFileToUriString();

  public List<String> find(ScriptSearch search) {
    List<String> scriptLocations = new ArrayList<String>();
    if(search.getDirectory().canRead()) {
      for(String script : scansDirectory.scan(search.getDirectory(), search.getIncludes(), search.getExcludes())) {
        scriptLocations.add(convertsFileToUriString.convert(new File(search.getDirectory(),script)));
      }
    }
    return scriptLocations;
  }
}
