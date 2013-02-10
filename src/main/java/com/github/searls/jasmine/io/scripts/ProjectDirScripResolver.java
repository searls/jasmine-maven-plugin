package com.github.searls.jasmine.io.scripts;

import com.github.searls.jasmine.model.ScriptSearch;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import com.github.searls.jasmine.AbstractJasmineMojo;

public class ProjectDirScripResolver extends AbstractScriptResolver {

	private final String srcDirectoryName;
	private final String specDirectoryName;
	
  public ProjectDirScripResolver(AbstractJasmineMojo config) throws IOException {
  	
  	this.baseDir = config.getMavenProject().getBasedir();
  	this.preloads = config.getPreloadSources();
    this.scriptSearchSpecs = config.getSpecs();
    this.scriptSearchSources = config.getSources();
    this.srcDirectoryName = config.getSrcDirectoryName();
    this.specDirectoryName = config.getSpecDirectoryName();
  }
  
  @Override
	public String getSourceDirectory() throws IOException {
    return this.srcDirectoryName;
  }

  @Override
	public String getSpecDirectoryPath() throws MalformedURLException {
    return this.specDirectoryName;
  }
}
