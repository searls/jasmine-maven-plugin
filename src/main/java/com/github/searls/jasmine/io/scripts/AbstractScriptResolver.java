package com.github.searls.jasmine.io.scripts;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractScriptResolver implements ScriptResolver {

  @Override
  public Set<String> getAllScripts() {
    LinkedHashSet<String> allScripts = new LinkedHashSet<String>();
    allScripts.addAll(this.getPreloads());
    allScripts.addAll(this.getSources());
    allScripts.addAll(this.getSpecs());
    return allScripts;
  }

}
