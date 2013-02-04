package com.github.searls.jasmine.io.scripts;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import com.github.searls.jasmine.io.RelativizesFilePaths;

public class RelativizesASetOfScripts {

  private RelativizesFilePaths relativizesFilePaths = new RelativizesFilePaths();

  public Set<String> relativize(File from, Set<String> absoluteScripts) throws IOException {
    Set<String> relativeScripts = new LinkedHashSet<String>();
    for (String absoluteScript : absoluteScripts) {
      File script = new File(normalize(absoluteScript));
      if(!webUrl(absoluteScript) && script.exists()) {
        relativeScripts.add(relativizesFilePaths.relativize(from, script));
      } else {
        relativeScripts.add(absoluteScript);
      }
    }
    return relativeScripts;
  }

  private String normalize(String absoluteScript) {
    String strip = "file:" + (File.separatorChar == '/' ? "" : "/");
    return stripStart(absoluteScript,strip);
  }

  private boolean webUrl(String script) {
    return startsWithAny(script,new String[]{"http:","https:"});
  }

}
