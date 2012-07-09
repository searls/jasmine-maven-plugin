package com.github.searls.jasmine.io.scripts;

import static org.apache.commons.lang.StringUtils.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;

import com.github.searls.jasmine.io.RelativizesFilePaths;

public class RelativizesASetOfScripts {

	private RelativizesFilePaths relativizesFilePaths = new RelativizesFilePaths();
	
	public Set<String> relativize(File from, Set<String> absoluteScripts) throws IOException {
		Set<String> relativeScripts = new LinkedHashSet<String>();
		for (String absoluteScript : absoluteScripts) {
			File script = new File(URI.create(absoluteScript));
			if(!webUrl(absoluteScript) && script.exists()) {
				relativeScripts.add(relativizesFilePaths.relativize(from, script));
			} else {
				relativeScripts.add(absoluteScript);
			}
		}
		return relativeScripts;
	}

	private boolean webUrl(String script) {
		return startsWithAny(script,new String[]{"http:","https:"});
	}

}
