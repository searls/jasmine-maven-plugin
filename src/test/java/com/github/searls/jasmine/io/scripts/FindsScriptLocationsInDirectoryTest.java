package com.github.searls.jasmine.io.scripts;

import com.github.searls.jasmine.io.ScansDirectory;
import com.github.searls.jasmine.model.ImmutableScriptSearch;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FindsScriptLocationsInDirectoryTest {

  private static final List<String> INCLUDES = asList("So in");
  private static final List<String> EXCLUDES = asList("So out");
  private static final String FILE_LOCATION = "blah/a.js";

  @Mock
  private ScansDirectory scansDirectory;

  @Mock
  private ConvertsFileToUriString convertsFileToUriString;

  @Spy
  private File directory = new File("Not quite a real directory");

  @InjectMocks
  private FindsScriptLocationsInDirectory subject;

  @Before
  public void directoryStubbing() {
    when(directory.canRead()).thenReturn(true);
  }

  @Test
  public void returnsEmptyWhenDirectoryDoesNotExist() throws IOException {
    List<String> result = subject.find(
      ImmutableScriptSearch.builder().directory(new File("No way does this file exist")).build()
    );

    assertThat(result).isEmpty();
  }

  @Test
  public void addsScriptLocationScannerFinds() throws IOException {
    String expected = "full blown file";
    when(scansDirectory.scan(directory, INCLUDES, EXCLUDES)).thenReturn(asList(FILE_LOCATION));
    when(convertsFileToUriString.convert(new File(directory, FILE_LOCATION))).thenReturn(expected);

    List<String> result = subject.find(
      ImmutableScriptSearch.builder().directory(directory).includes(INCLUDES).excludes(EXCLUDES).build()
    );

    assertThat(result).contains(expected);
  }

}
