package com.github.searls.jasmine.io;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class RelativizesFilePathsTest {

  private RelativizesFilePaths subject = new RelativizesFilePaths();

  private File from;
  private File to;

  @Test
  public void resolvesWithinDir() throws IOException {
    from = new File("/panda/");
    to = new File("/panda/pants.txt");

    String result = subject.relativize(from, to);

    Assertions.assertThat(result).isEqualTo("pants.txt");
  }

  @Test
  public void resolvesSubDir() throws IOException {
    from = new File("/panda/");
    to = new File("/panda/target/jasmine/");

    String result = subject.relativize(from, to);

    Assertions.assertThat(result).isEqualTo("target/jasmine");
  }


  @Test
  public void resolvesDeepChild() throws IOException {
    from = new File(slash("/Volumes/blah/Users/justin/code/workspaces/jasmine_maven/jasmine-maven-plugin/src/test/resources/examples/jasmine-webapp-coffee/target/jasmine"));
    to = new File(slash("/Volumes/blah/Users/justin/code/workspaces/jasmine_maven/jasmine-maven-plugin/src/test/resources/examples/jasmine-webapp-coffee/src/test/javascript/fun-spec.js"));

    String result = subject.relativize(from, to);

    Assertions.assertThat(result).isEqualTo("../../src/test/javascript/fun-spec.js");
  }

  private String slash(String s) {
    return s.replace('/', File.separatorChar);
  }

}
