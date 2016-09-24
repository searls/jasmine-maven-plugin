package com.github.searls.jasmine.io.scripts;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Named
public class ResolvesLocationOfPreloadSources {

  private final ConvertsFileToUriString convertsFileToUriString;

  @Inject
  public ResolvesLocationOfPreloadSources(ConvertsFileToUriString convertsFileToUriString) {

    this.convertsFileToUriString = convertsFileToUriString;
  }

  public List<String> resolve(List<String> preloadSources, File sourceDir, File specDir) {
    List<String> sources = new ArrayList<String>();
    if (preloadSources != null) {
      for (String source : preloadSources) {
        File sourceFile = getAsFile(sourceDir, specDir, source);
        if (sourceFile.exists()) {
          sources.add(convertsFileToUriString.convert(sourceFile));
        } else {
          sources.add(source);
        }
      }
    }
    return sources;
  }

  private File getAsFile(File sourceDir, File specDir, String source) {
    File sourceFile = new File(sourceDir, source);

    if (!sourceFile.exists()) {
      sourceFile = new File(specDir, source);
    }

    if (!sourceFile.exists()) {
      sourceFile = new File(source);
    }

    return sourceFile;
  }
}
