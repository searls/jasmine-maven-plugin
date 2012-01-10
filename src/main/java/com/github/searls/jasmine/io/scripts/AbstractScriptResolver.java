package com.github.searls.jasmine.io.scripts;

import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.model.ScriptSearch;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractScriptResolver implements ScriptResolver {
	private Set<String> sources;
	private Set<String> specs;
	protected File baseDir;
	protected ScriptSearch scriptSearchSources;
	protected ScriptSearch scriptSearchSpecs;
	protected List<String> preloads;
	protected RelativizesASetOfScripts relativizer = new RelativizesASetOfScripts();
	protected RelativizesFilePaths relativizesFilePaths = new RelativizesFilePaths();

	public void resolveScripts() throws IOException {
		ResolvesLocationOfPreloadSources resolvesLocationOfPreloadSources = new ResolvesLocationOfPreloadSources();
		FindsScriptLocationsInDirectory findsScriptLocationsInDirectory = new FindsScriptLocationsInDirectory();

		setScriptsToPreload(new LinkedHashSet<String>(resolvesLocationOfPreloadSources.resolve(preloads, scriptSearchSources.getDirectory(), scriptSearchSpecs.getDirectory())));
		setSources(new LinkedHashSet<String>(findsScriptLocationsInDirectory.find(scriptSearchSources)));
		setSpecs(new LinkedHashSet<String>(findsScriptLocationsInDirectory.find(scriptSearchSpecs)));

	}

	public Set<String> getPreloads() {
		return this.scriptsToPreload;
	}

	public Set<String> getSources() {
		return this.sources;
	}

	public Set<String> getSpecs() {
		return this.specs;
	}

	public Set<String> getAllScripts() {
		return addAllScripts(scriptsToPreload, sources, specs);
	}

	public String getSourceDirectory() throws IOException {
		return scriptSearchSources.getDirectory().toURI().toURL().toString();
	}

	public String getSpecDirectoryPath() throws MalformedURLException {
		return scriptSearchSpecs.getDirectory().toURI().toURL().toString();
	}

	public Set<String> getSourcesRelativePath() throws IOException {
		return relativizer.relativize(baseDir, this.sources);
	}

	public Set<String> getSpecsRelativePath() throws IOException {
		return relativizer.relativize(baseDir, this.specs);
	}

	public Set<String> getPreloadsRelativePath() throws IOException {
		return relativizer.relativize(baseDir, this.scriptsToPreload);
	}

	public Set<String> getAllScriptsRelativePath() throws IOException {
		return addAllScripts(getPreloadsRelativePath(), getSourcesRelativePath(), getSpecsRelativePath());
	}

	public String getSourceDirectoryRelativePath() throws IOException {
		return relativizesFilePaths.relativize(baseDir, scriptSearchSources.getDirectory());
	}

	public String getSpecDirectoryRelativePath() throws IOException {
		return relativizesFilePaths.relativize(baseDir, scriptSearchSpecs.getDirectory());
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
