package com.github.searls.jasmine.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("unused")
public class RelativizesFilePaths {

  public String relativize(File from, File to) throws IOException {
    String fromPath = from.getCanonicalPath();
    String toPath = to.getCanonicalPath();

    String root = StringUtils.getCommonPrefix(new String[] { fromPath, toPath });
    StringBuffer result = new StringBuffer();
    if (fromPathIsNotADirectAncestor(fromPath, root)) {
      for (String dir : divergentDirectories(root, fromPath)) {
        result.append("..").append(File.separator);
      }
    }
    result.append(pathAfterRoot(toPath, root));

    return convertSlashes(trimLeadingSlashIfNecessary(result));
  }

  private String convertSlashes(String path) {
    return path.replace(File.separatorChar, '/');
  }

  private boolean fromPathIsNotADirectAncestor(String fromPath, String root) {
    return !StringUtils.equals(root, fromPath);
  }

  private String[] divergentDirectories(String root, String fullPath) {
    return pathAfterRoot(fullPath, root).split(StringEscapeUtils.escapeJava(File.separator));
  }

  private String pathAfterRoot(String path, String root) {
    return StringUtils.substringAfterLast(path, root);
  }

  private String trimLeadingSlashIfNecessary(StringBuffer result) {
    return StringUtils.removeStart(result.toString(),File.separator);
  }

}
