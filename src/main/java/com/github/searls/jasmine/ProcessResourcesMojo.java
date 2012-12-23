package com.github.searls.jasmine;

import java.io.File;
import java.io.IOException;

import com.github.searls.jasmine.coffee.CompilesAllCoffeeInDirectory;
import com.github.searls.jasmine.io.DirectoryCopier;
import com.github.searls.jasmine.model.OverlayScriptSearch;
import com.github.searls.jasmine.model.ScriptSearch;

/**
 * @goal resources
 * @phase process-resources
 */
public class ProcessResourcesMojo extends AbstractJasmineMojo {

  public static final String MISSING_DIR_WARNING =
    "JavaScript source folder was expected but was not found. " +
    "Set configuration property `jsSrcDir` to the directory containing your JavaScript sources. " +
    "Skipping jasmine:resources processing.";

  private DirectoryCopier directoryCopier = new DirectoryCopier();
  private CompilesAllCoffeeInDirectory compilesAllCoffeeInDirectory = new CompilesAllCoffeeInDirectory();

  public void run() throws IOException {
    getLog().info("Processing JavaScript Sources");
    if (sources.getDirectory().exists()) {
      copySource(sources, srcDirectoryName);
      copyWarOverlaySources();
    } else {
      getLog().warn(MISSING_DIR_WARNING);
    }
  }
  
  private void copySource(ScriptSearch scriptSearch, String srcDirectoryName) throws IOException {
      File destination = new File(jasmineTargetDir, srcDirectoryName);
      directoryCopier.copyDirectory(scriptSearch.getDirectory(), destination);
      compilesAllCoffeeInDirectory.compile(destination);
  }
  
  private void copyWarOverlaySources() throws IOException {
	  for(OverlayScriptSearch overlayScriptSearch:warOverlays) {
		if(overlayScriptSearch.getDirectory().exists()) {
			copySource(overlayScriptSearch, overlayScriptSearch.getSrcDirectoryName());
		}
	  }
  }

}
