package com.github.searls.jasmine.server;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

public class MetaInfContext extends WebAppContext
{
  private File emptyDir;
  private List<File> jars;

  public MetaInfContext() throws IOException {
    setConfigurations(new Configuration[]{
      new JasmineJarConfiguration(),
      new WebXmlConfiguration(),
      new MetaInfConfiguration(),
    });

    emptyDir = createEmptyDir();
    setWar(emptyDir.getAbsolutePath());
  }

  public List<File> getJars() {
    return jars;
  }

  public void setJars(final List<File> jars) {
    this.jars = jars;
  }

  private static File createEmptyDir() throws IOException {
    File dir = File.createTempFile("jasmine", Long.toString(System.nanoTime()));
    if ( !dir.delete() || !dir.mkdir() ) {
      throw new IOException("Unable to create temp directory at "+dir.getAbsolutePath());
    }
    return dir;
  }

  @Override
  protected void doStart() throws Exception {
    if ( !emptyDir.exists() ) {
      emptyDir = createEmptyDir();
    } else {
      FileUtils.cleanDirectory(emptyDir);
    }

    setShutdown(false);
    super.doStart();
  }

  @Override
  protected void doStop() throws Exception {
    setShutdown(false);
    //just wait a little while to ensure no requests are still being processed
    Thread.sleep(500L);
    super.doStop();
    FileUtils.deleteDirectory(emptyDir);
  }
}
