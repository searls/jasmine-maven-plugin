package com.github.searls.jasmine.io;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

@SuppressWarnings("unchecked")
public class ScansDirectoryIntegrationTest {

  private static final List<String> DEFAULT_EXCLUDES = Collections.EMPTY_LIST;
  private static final List<String> DEFAULT_INCLUDES = asList("**/*.js");

  private ScansDirectory subject = new ScansDirectory();

  private CreatesTempDirectories createsTempDirectories = new CreatesTempDirectories();
  private File directory = createsTempDirectories.create("someDir");

  @Test
  public void shouldReturnNothingWhenThereIsNothing() {
    List<String> results = subject.scan(directory, DEFAULT_INCLUDES, DEFAULT_EXCLUDES);

    assertThat(results,is(DEFAULT_EXCLUDES));
  }

  @Test
  public void shouldReturnMatchingJS() {
    String expected = "blah.js";
    createFile(expected);

    List<String> results = subject.scan(directory, DEFAULT_INCLUDES, DEFAULT_EXCLUDES);

    assertThat(results,hasItem(expected));
  }

  @Test
  public void shouldNotReturnSomeHtml() {
    String expected = "blah.html";
    createFile(expected);

    List<String> results = subject.scan(directory, DEFAULT_INCLUDES, DEFAULT_EXCLUDES);

    assertThat(results,not(hasItem(expected)));
  }

  @Test
  public void shouldExcludeExplicitlyExcludedJs() {
    String expected = "pants.js";
    createFile(expected);

    List<String> results = subject.scan(directory, DEFAULT_INCLUDES, asList("pants.js"));

    assertThat(results,not(hasItem(expected)));
  }

  @Test
  public void shouldNotIncludeHiddenSvnFiles() {
    createSubDir(".svn");
    String expected = ".svn/logs";
    createFile(expected);

    List<String> results = subject.scan(directory, asList("**"), DEFAULT_EXCLUDES);

    assertThat(results,not(hasItem(expected)));
  }

  @Test
  public void sortsAnExactMatchOverALaterExactMatch() {
    createFile("a.js", "b.js");

    List<String> result = subject.scan(directory,asList("b.js", "a.js"),Collections.EMPTY_LIST);

    assertThat(result, is(asList("b.js", "a.js")));
  }

  @Test
  public void alphabetizesMatches() {
    createSubDir("c");
    createFile("c/d","c/a","b","a");

    List<String> result = subject.scan(directory,asList("**"),Collections.EMPTY_LIST);

    assertThat(result, is(asList("a","b","c"+File.separator+"a","c"+File.separator+"d")));
  }


  @Test
  public void matchesRootItemsWhenIncludeHasFileSeparator() {
    createSubDir("b");
    createFile("a.js","b/c.js");

    List<String> result = subject.scan(directory,ScansDirectory.DEFAULT_INCLUDES,Collections.EMPTY_LIST);

    assertThat(result, is(asList("a.js","b"+File.separator+"c.js")));
  }

  @Test
  public void matchesRootItemUnderSubfolder() {
    createSubDir("lib");
    createFile("b.js","lib/a.js");

    List<String> result = subject.scan(directory,asList("lib/**/*.js","**/*.js"),Collections.EMPTY_LIST);

    assertThat(result, is(asList("lib"+File.separator+"a.js","b.js")));
  }

  @Test
  public void sortsARealWorldTestAsExpected() {
    createSubDir("category",
        "customer",
        "customer/customcode",
        "jquery","jquery/cookie",
        "jquery/jcrumb",
        "jquery/underscore",
        "yui2",
        "yui2/animation",
        "yui2/paginator",
        "yui2/selector",
        "yui2/yahoo-dom-event",
        "panda");
    createFile("breadcrumbs.js",
        "category/category-management.js",
        "a.js",
        "customer/customcode/custom-code-preferences.js",
        "customer/view.js",
        "b.js",
        "jquery/cookie/jquery.cookie.js",
        "jquery/jcrumb/jquery.jcrumbs-1.0.js",
        "jquery/underscore/underscore-min-1.1.2.js",
        "utils.js",
        "yui2/animation/animation-min.js",
        "yui2/paginator/paginator.js",
        "yui2/selector/selector-min.js",
        "jquery/jquery.formatCurrency-1.4.0.min.js",
        "yui2/yahoo-dom-event/yahoo-dom-event.js",
        "c.js",
        "panda/sad.js");

    List<String> result = subject.scan(directory,asList("jquery/**/*.js", "yui2/**/*.js", "**/*.js"),Collections.EMPTY_LIST);

    assertThat(result, is(slashify("jquery/cookie/jquery.cookie.js",
        "jquery/jcrumb/jquery.jcrumbs-1.0.js",
        "jquery/jquery.formatCurrency-1.4.0.min.js",
        "jquery/underscore/underscore-min-1.1.2.js",
        "yui2/animation/animation-min.js",
        "yui2/paginator/paginator.js",
        "yui2/selector/selector-min.js",
        "yui2/yahoo-dom-event/yahoo-dom-event.js",
        "a.js",
        "b.js",
        "breadcrumbs.js",
        "c.js",
        "category/category-management.js",
        "customer/customcode/custom-code-preferences.js",
        "customer/view.js",
        "panda/sad.js",
        "utils.js")));
  }

  private List<String> slashify(String...scripts){
    List<String> slashed = new ArrayList<String>();
    for (String s : scripts) {
      slashed.add(s.replace('/', File.separatorChar));
    }
    return slashed;
  }

  private void createFile(String... paths) {
    for (String path : paths) {
      try {
        new File(directory,path).createNewFile();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void createSubDir(String... names) {
    for (String name : names) {
      new File(directory,name).mkdir();
    }
  }
}
