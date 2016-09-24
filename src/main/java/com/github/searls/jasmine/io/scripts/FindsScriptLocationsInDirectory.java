package com.github.searls.jasmine.io.scripts;

import com.github.searls.jasmine.io.ScansDirectory;
import com.github.searls.jasmine.model.ScriptSearch;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Named
public class FindsScriptLocationsInDirectory {

  private final ScansDirectory scansDirectory;
  private final ConvertsFileToUriString convertsFileToUriString;

  @Inject
  public FindsScriptLocationsInDirectory(ScansDirectory scansDirectory,
                                         ConvertsFileToUriString convertsFileToUriString) {
    this.scansDirectory = scansDirectory;
    this.convertsFileToUriString = convertsFileToUriString;
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
