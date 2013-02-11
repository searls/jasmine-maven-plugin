package com.github.searls.jasmine.io.scripts;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.stripStart;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.searls.jasmine.io.RelativizesFilePaths;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RelativizesASetOfScripts.class)
public class RelativizesASetOfScriptsTest {

  @InjectMocks
  private final RelativizesASetOfScripts subject = new RelativizesASetOfScripts();

  @Mock
  private RelativizesFilePaths relativizesFilePaths;

  @Mock
  private File from;

  @Captor
  private ArgumentCaptor<String> fileNameCaptor;

  @Test
  public void loopsThroughStringsAndRelativizes() throws Exception {
    stubbingForFiles(true);
    HashSet<String> sources = new HashSet<String>(asList("a", "b"));
    when(relativizesFilePaths.relativize(eq(from), argThat(is(fileNamed("a"))))).thenReturn("alpha");
    when(relativizesFilePaths.relativize(eq(from), argThat(is(fileNamed("b"))))).thenReturn("beta");

    Set<String> result = subject.relativize(from, sources);

    assertThat(result, is(instanceOf(LinkedHashSet.class)));
    assertThat(result, hasItems("alpha", "beta"));
  }

  @Test
  public void removesLeadingFileProtocol() throws Exception {
    stubbingForFiles(true);
    String name = stripFile("file:/c:/panda");
    HashSet<String> sources = new HashSet<String>(asList(name));
    when(relativizesFilePaths.relativize(eq(from), argThat(is(fileNamed(name))))).thenReturn("panda");

    Set<String> result = subject.relativize(from, sources);

    assertThat(result, is(instanceOf(LinkedHashSet.class)));
    assertThat(result, hasItems("panda"));
  }

  @Test
  public void ignoresLeadingHttpProtocol() throws Exception {
    stubbingForFiles(true);
    HashSet<String> sources = new HashSet<String>(asList("http://google.com"));
    when(relativizesFilePaths.relativize(isA(File.class),isA(File.class))).thenThrow(new RuntimeException("Not supported!"));

    Set<String> result = subject.relativize(from, sources);

    assertThat(result, is(instanceOf(LinkedHashSet.class)));
    assertThat(result, hasItems("http://google.com"));
  }

  @Test
  public void ignoresLeadingHttpsProtocol() throws Exception {
    stubbingForFiles(true);
    HashSet<String> sources = new HashSet<String>(asList("https://google.com"));
    when(relativizesFilePaths.relativize(isA(File.class),isA(File.class))).thenThrow(new RuntimeException("Not supported!"));

    Set<String> result = subject.relativize(from, sources);

    assertThat(result, is(instanceOf(LinkedHashSet.class)));
    assertThat(result, hasItems("https://google.com"));
  }

  @Test
  public void ifFileDoesNotExistThenPrintAsIs() throws Exception {
    stubbingForFiles(false);
    HashSet<String> sources = new HashSet<String>(asList("file:///dusseldorf"));
//    when(relativizesFilePaths.relativize(isA(File.class),isA(File.class))).thenThrow(new RuntimeException("Must exist to relativize!"));

    Set<String> result = subject.relativize(from, sources);

    assertThat(result, is(instanceOf(LinkedHashSet.class)));
    assertThat(result, hasItems("file:///dusseldorf"));

  }

  private void stubbingForFiles(boolean exists) throws Exception {
    final File file = spy(new File(""));
    whenNew(File.class).withParameterTypes(String.class).withArguments(fileNameCaptor.capture()).thenAnswer(new Answer<File>(){
      public File answer(InvocationOnMock invocation) throws Throwable {
        when(file.toString()).thenReturn((String) invocation.getArguments()[0]);
        return file;
      }
    });

    when(file.exists()).thenReturn(exists);
  }

  private static TypeSafeMatcher<File> fileNamed(final String name) {
    return new TypeSafeMatcher<File>() {
      @Override
			public boolean matchesSafely(File file) {
        return name.equals(file.toString());
      }
      public void describeTo(Description description) {}
    };
  }

  private String stripFile(String absoluteScript) {
    String strip = "file:" + (File.separatorChar == '/' ? "" : "/");
    return stripStart(absoluteScript,strip);
  }
}
