package com.github.searls.jasmine.thirdpartylibs;

import org.apache.maven.artifact.Artifact;
import org.mockito.Mockito;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.when;

public class ProjectClassLoaderHelper {

  static public ClassLoader projectClassLoaderOf(String jarPath) {
    Artifact artifact = Mockito.mock(Artifact.class);
    Set<Artifact> artifacts = new HashSet<Artifact>();
    artifacts.add(artifact);
    File jarFile = new File(jarPath);
    when(artifact.getFile()).thenReturn(jarFile);
    return new ProjectClassLoaderFactory(artifacts).create();
  }
}
