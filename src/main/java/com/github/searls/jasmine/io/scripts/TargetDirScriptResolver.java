package com.github.searls.jasmine.io.scripts;

import com.github.searls.jasmine.AbstractJasmineMojo;
import com.github.searls.jasmine.model.ScriptSearch;

import java.io.File;
import java.io.IOException;

public class TargetDirScriptResolver extends AbstractScriptResolver {


	public TargetDirScriptResolver(AbstractJasmineMojo configuration) throws IOException {
		this.baseDir = configuration.getJasmineTargetDir();
		this.preloads = configuration.getPreloadSources();
		this.scriptSearchSources = searchForDir(new File(this.baseDir, configuration.getSrcDirectoryName()), configuration.getSources());
		this.scriptSearchSpecs = searchForDir(new File(this.baseDir, configuration.getSpecDirectoryName()), configuration.getSpecs());

		resolveScripts();
	}

	private ScriptSearch searchForDir(File dir, ScriptSearch search) {
		return new ScriptSearch(dir, search.getIncludes(), search.getExcludes());
	}
}
