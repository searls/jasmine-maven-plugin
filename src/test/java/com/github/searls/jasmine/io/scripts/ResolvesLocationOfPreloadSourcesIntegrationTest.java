package com.github.searls.jasmine.io.scripts;

import com.github.searls.jasmine.io.CreatesTempDirectories;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ResolvesLocationOfPreloadSourcesIntegrationTest {

  private static final String SPEC = "spec";
  private static final String SOURCE = "source";
  private static final String SEPARATED_DIR = "separatedDir";

  private ResolvesLocationOfPreloadSources subject = new ResolvesLocationOfPreloadSources();

  private CreatesTempDirectories createsTempDirectories = new CreatesTempDirectories();
  private File sourceDir = createsTempDirectories.create(SOURCE);
  private File specDir = createsTempDirectories.create(SPEC);
  private File separatedDir = createsTempDirectories.create(SEPARATED_DIR);

  @After
  public void deleteTempDirs() {
    sourceDir.delete();
    specDir.delete();
    separatedDir.delete();
  }

  @Test
  public void returnsListWhenSourcesIsNull() {
    List<String> result = subject.resolve(null, sourceDir, specDir);

    Assertions.assertThat(result).isNotNull();
  }


  @Test
  public void loadsSourceFileWhenItExistsUnderSourceAndSpec() throws IOException {
    String expected = "panda";
    new File(sourceDir, expected).createNewFile();
    new File(specDir, expected).createNewFile();
    List<String> preloadSources = Arrays.asList(expected);

    List<String> result = subject.resolve(preloadSources, sourceDir, specDir);


    Assertions.assertThat(result).hasSize(1);
    Assertions.assertThat(result.get(0)).contains(SOURCE);
    Assertions.assertThat(result.get(0)).contains(expected);
  }

  @Test
  public void loadsSpecFileWhenItExistsUnderSpec() throws IOException {
    String expected = "panda";
    new File(specDir, expected).createNewFile();
    List<String> preloadSources = Arrays.asList(expected);

    List<String> result = subject.resolve(preloadSources, sourceDir, specDir);


    Assertions.assertThat(result).hasSize(1);
    Assertions.assertThat(result.get(0)).contains(SPEC);
    Assertions.assertThat(result.get(0)).contains(expected);
  }

  @Test
  public void loadsExistentFileWithURIIfItIsNotInEitherSourceAndSpecFolders() throws Exception {
    String expected = "panda";
    new File(separatedDir, expected).createNewFile();
    List<String> preloadSources = Arrays.asList(separatedDir.getAbsolutePath() + File.separator + expected);

    List<String> result = subject.resolve(preloadSources, sourceDir, specDir);

    Assertions.assertThat(result).hasSize(1);
    Assertions.assertThat(result.get(0)).contains("file:/");
  }

  @Test
  public void printsAsIsWhenItDoesNotExist() {
    String expected = "telnet://woahItsTelnet";
    List<String> preloadSources = Arrays.asList(expected);

    List<String> result = subject.resolve(preloadSources, sourceDir, specDir);

    Assertions.assertThat(result).hasSize(1);
    Assertions.assertThat(result.get(0)).isEqualTo(expected);
  }
//
}
