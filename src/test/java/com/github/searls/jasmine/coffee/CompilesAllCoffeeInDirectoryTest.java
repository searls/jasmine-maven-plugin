package com.github.searls.jasmine.coffee;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.searls.jasmine.io.FileUtilsWrapper;

@RunWith(MockitoJUnitRunner.class)
public class CompilesAllCoffeeInDirectoryTest {

  @InjectMocks private CompilesAllCoffeeInDirectory subject = new CompilesAllCoffeeInDirectory();

  @Mock private FileUtilsWrapper fileUtilsWrapper;
  @Mock private CompilesCoffeeInPlace compilesCoffeeInPlace;

  @Mock File directory;
  @Mock File file;
  @Mock File anotherFile;

  @Test
  public void compilesEachFile() throws IOException {
    when(fileUtilsWrapper.listFiles(directory, CompilesAllCoffeeInDirectory.COFFEE_EXTENSIONS, true)).thenReturn(asList(file,anotherFile));

    subject.compile(directory);

    verify(compilesCoffeeInPlace).compile(file);
    verify(compilesCoffeeInPlace).compile(anotherFile);
  }


}
