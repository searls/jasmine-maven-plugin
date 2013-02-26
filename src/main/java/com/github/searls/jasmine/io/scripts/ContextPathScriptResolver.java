package com.github.searls.jasmine.io.scripts;

import java.util.LinkedHashSet;
import java.util.Set;

public class ContextPathScriptResolver implements ScriptResolver {

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
  public String getSourceDirectory() throws ScriptResolverException {
    return this.sourceContextPath;
  }

  @Override
  public String getSpecDirectory() throws ScriptResolverException {
    return this.specContextPath;
  }

  @Override
  public Set<String> getSources() throws ScriptResolverException {
    return relativeToContextPath(
        this.scriptResolver.getSourceDirectory(),
        this.sourceContextPath,
        this.scriptResolver.getSources());
  }

  @Override
  public Set<String> getSpecs() throws ScriptResolverException {
    return relativeToContextPath(
        this.scriptResolver.getSpecDirectory(),
        this.specContextPath,
        this.scriptResolver.getSpecs());
  }

  @Override
  public Set<String> getPreloads() throws ScriptResolverException {
    Set<String> scripts = this.scriptResolver.getPreloads();
    scripts = relativeToContextPath(
        this.scriptResolver.getSourceDirectory(),
        this.sourceContextPath,
        scripts);
    scripts = relativeToContextPath(
        this.scriptResolver.getSpecDirectory(),
        this.specContextPath,
        scripts);
    return scripts;
  }

  @Override
  public Set<String> getAllScripts() throws ScriptResolverException {
    LinkedHashSet<String> allScripts = new LinkedHashSet<String>();
    allScripts.addAll(this.getPreloads());
    allScripts.addAll(this.getSources());
    allScripts.addAll(this.getSpecs());
    return allScripts;
  }

  private Set<String> relativeToContextPath(String realPath, String contextPath, Set<String> absoluteScripts) {
    Set<String> relativeScripts = new LinkedHashSet<String>();
    for (String absoluteScript : absoluteScripts) {
      relativeScripts.add(absoluteScript.replaceFirst(realPath, contextPath));
    }
    return relativeScripts;
  }
}
