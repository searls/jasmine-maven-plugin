package com.github.searls.jasmine.io.scripts;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.model.ScriptSearch;

public abstract class AbstractScriptResolver implements ScriptResolver {
	private Set<String> sources;
	private Set<String> specs;
	protected File baseDir;
	protected ScriptSearch scriptSearchSources;
	protected ScriptSearch scriptSearchSpecs;
	protected List<String> preloads;
	protected RelativizesASetOfScripts relativizer = new RelativizesASetOfScripts();
	protected RelativizesFilePaths relativizesFilePaths = new RelativizesFilePaths();

	@Override
	public void resolveScripts() throws IOException {
		ResolvesLocationOfPreloadSources resolvesLocationOfPreloadSources = new ResolvesLocationOfPreloadSources();
		FindsScriptLocationsInDirectory findsScriptLocationsInDirectory = new FindsScriptLocationsInDirectory();

		this.setScriptsToPreload(new LinkedHashSet<String>(resolvesLocationOfPreloadSources.resolve(this.preloads, this.scriptSearchSources.getDirectory(), this.scriptSearchSpecs.getDirectory())));

		this.setSources(new LinkedHashSet<String>(findsScriptLocationsInDirectory.find(this.scriptSearchSources)));
		this.sources.removeAll(this.scriptsToPreload);

		this.setSpecs(new LinkedHashSet<String>(findsScriptLocationsInDirectory.find(this.scriptSearchSpecs)));
		this.specs.removeAll(this.scriptsToPreload);
	}

	@Override
	public String getSourceDirectory() throws IOException {
		return this.scriptSearchSources.getDirectory().toURI().toURL().toString();
	}

	@Override
	public String getSpecDirectory() throws MalformedURLException {
		return this.scriptSearchSpecs.getDirectory().toURI().toURL().toString();
	}

	@Override
	public Set<String> getSources() throws IOException {
		return this.relativizer.relativize(this.baseDir, this.sources);
	}

	@Override
	public Set<String> getSpecs() throws IOException {
		return this.relativizer.relativize(this.baseDir, this.specs);
	}

	@Override
	public Set<String> getPreloads() throws IOException {
		return this.relativizer.relativize(this.baseDir, this.scriptsToPreload);
	}

	@Override
	public Set<String> getAllScripts() throws IOException {
		return this.addAllScripts(this.getPreloads(), this.getSources(), this.getSpecs());
	}

	private Set<String> addAllScripts(Set<String> preloadedSources, Set<String> sources, Set<String> specs) {
		LinkedHashSet<String> allScripts = new LinkedHashSet<String>();
		allScripts.addAll(preloadedSources);
		allScripts.addAll(sources);
		allScripts.addAll(specs);
		return allScripts;
	}

	private Set<String> scriptsToPreload;

	public void setScriptsToPreload(Set<String> scriptsToPreload) {
		this.scriptsToPreload = scriptsToPreload;
	}

	public void setSources(Set<String> sources) {
		this.sources = sources;
	}

	public void setSpecs(Set<String> specs) {
		this.specs = specs;
	}
}
