package com.github.searls.jasmine.io.scripts;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.searls.jasmine.io.RelativizesFilePaths;

public class RelativizesASetOfScripts {

	private final RelativizesFilePaths relativizesFilePaths = new RelativizesFilePaths();

	public Set<String> relativize(File from, Set<String> absoluteScripts) throws IOException {
		Set<String> relativeScripts = new LinkedHashSet<String>();
		for (String absoluteScript : absoluteScripts) {
			File script = new File(this.normalize(absoluteScript));
			if(!this.webUrl(absoluteScript) && script.exists()) {
				relativeScripts.add(this.relativizesFilePaths.relativize(from, script));
			} else {
				relativeScripts.add(absoluteScript);
			}
		}
		return relativeScripts;
	}

	private String normalize(String absoluteScript) {
		String strip = "file:" + (File.separatorChar == '/' ? "" : "/");
		return StringUtils.stripStart(absoluteScript,strip);
	}

	private boolean webUrl(String script) {
		return StringUtils.startsWithAny(script,new String[]{"http:","https:"});
	}

}
