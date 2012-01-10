package com.github.searls.jasmine.io.scripts;

import com.github.searls.jasmine.model.ScriptSearch;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProjectDirScripResolver extends AbstractScriptResolver {

	public ProjectDirScripResolver(File projectBaseDir, ScriptSearch sources, ScriptSearch specs, List<String> preloads) throws IOException {
		this.preloads = preloads;
		this.scriptSearchSpecs = specs;
		this.scriptSearchSources = sources;
		this.baseDir = projectBaseDir;
	}
}
