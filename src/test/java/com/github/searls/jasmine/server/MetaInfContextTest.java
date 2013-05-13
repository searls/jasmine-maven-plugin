package com.github.searls.jasmine.server;

import java.io.File;

import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MetaInfContextTest
{
  @Test
  public void testConstructor() throws Exception {
    MetaInfContext context = new MetaInfContext();

    Configuration[] configurations = context.getConfigurations();
    assertContainsConfig(configurations, MetaInfConfiguration.class);
    assertContainsConfig(configurations, JasmineJarConfiguration.class);
    assertContainsConfig(configurations, WebXmlConfiguration.class);

    File actualWarFile = new File(context.getWar());
    assertTrue("Temp directory does not exists",actualWarFile.exists());
    assertTrue("Temp directory is not a directory", actualWarFile.isDirectory());
    assertEquals("Temp directory is not empty",0, actualWarFile.list().length);

    assertFalse(context.isCopyWebDir());
  }

  private void assertContainsConfig(final Configuration[] configurations,
                                    final Class<? extends Configuration> expected) {
    final String failMessage = "No configuration of class " + expected.getCanonicalName() + " found";
    Assert.assertNotNull(failMessage, configurations);
    for(Configuration config : configurations) {
      if ( expected.isAssignableFrom(config.getClass())) {
        return;
      }
    }
    Assert.fail(failMessage);
  }

}
