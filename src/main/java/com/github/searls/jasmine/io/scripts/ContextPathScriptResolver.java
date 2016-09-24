package com.github.searls.jasmine.io.scripts;

import java.util.LinkedHashSet;
import java.util.Set;

public class ContextPathScriptResolver extends AbstractScriptResolver {

  private static final String BASE_DIRECTORY = "";

  private final ScriptResolver scriptResolver;
  private final String sourceContextPath;
  private final String specContextPath;

  public ContextPathScriptResolver(ScriptResolver scriptResolver,
                                   String sourceContextPath,
                                   String specContextPath) {
    this.scriptResolver = scriptResolver;
    this.sourceContextPath = sourceContextPath;
    this.specContextPath = specContextPath;
  }

  @Override
  public String getSourceDirectory() {
    return this.sourceContextPath;
  }

  @Override
  public String getSpecDirectory() {
    return this.specContextPath;
  }

  @Override
  public String getBaseDirectory() {
    return BASE_DIRECTORY;
  }

  @Override
  public Set<String> getSources() {
    return relativeToContextPath(
      this.scriptResolver.getSourceDirectory(),
      this.sourceContextPath,
      this.scriptResolver.getSources());
  }

  @Override
  public Set<String> getSpecs() {
    return relativeToContextPath(
      this.scriptResolver.getSpecDirectory(),
      this.specContextPath,
      this.scriptResolver.getSpecs());
  }

  @Override
  public Set<String> getPreloads() {
    Set<String> scripts = this.scriptResolver.getPreloads();
    scripts = relativeToContextPath(
      this.scriptResolver.getSourceDirectory(),
      this.getSourceDirectory(),
      scripts);
    scripts = relativeToContextPath(
      this.scriptResolver.getSpecDirectory(),
      this.getSpecDirectory(),
      scripts);
    scripts = relativeToContextPath(
      this.scriptResolver.getBaseDirectory(),
      this.getBaseDirectory(),
      scripts);
    return scripts;
  }

  private Set<String> relativeToContextPath(String realPath, String contextPath, Set<String> absoluteScripts) {
    Set<String> relativeScripts = new LinkedHashSet<String>();
    for (String absoluteScript : absoluteScripts) {
      relativeScripts.add(absoluteScript.replace(realPath, contextPath));
    }
    return relativeScripts;
  }
}
