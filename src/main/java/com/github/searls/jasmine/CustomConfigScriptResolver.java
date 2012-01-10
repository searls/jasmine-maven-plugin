package com.github.searls.jasmine;

import com.github.searls.jasmine.io.scripts.ProjectDirScripResolver;
import com.github.searls.jasmine.model.ScriptSearch;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CustomConfigScriptResolver extends ProjectDirScripResolver {
	public CustomConfigScriptResolver(File basedir, ScriptSearch sources, ScriptSearch specs, List<String> preloadSources) throws IOException {
		super(basedir, sources, specs, preloadSources);
	}

	@Override
	public String getSourceDirectoryRelativePath() throws IOException {
		String relativePath = super.getSourceDirectoryRelativePath();
		return relativePath.substring(relativePath.lastIndexOf("/"), relativePath.length());
	}
}
