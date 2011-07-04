package com.github.searls.jasmine.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import static org.apache.commons.lang.StringUtils.*;

@SuppressWarnings("unused")
public class RelativizesFilePaths {

	public String relativize(File from, File to) throws IOException {
		String fromPath = from.getCanonicalPath();
		String toPath = to.getCanonicalPath();

		String root = getCommonPrefix(new String[] { fromPath, toPath });
		StringBuffer result = new StringBuffer();
		if (fromPathIsNotADirectAncestor(fromPath, root)) {
			for (String dir : divergentDirectories(root, fromPath)) {
				result.append("..").append(File.separator);
			}
		}
		result.append(pathAfterRoot(toPath, root));
		
		return trimLeadingSlashIfNecessary(result);
	}

	private boolean fromPathIsNotADirectAncestor(String fromPath, String root) {
		return !StringUtils.equals(root, fromPath);
	}

	private String[] divergentDirectories(String root, String fullPath) {
		return pathAfterRoot(fullPath, root).split(File.separator);
	}

	private String pathAfterRoot(String path, String root) {
		return substringAfterLast(path, root);
	}

	private String trimLeadingSlashIfNecessary(StringBuffer result) {
		return removeStart(result.toString(),File.separator);
	}

}
