package com.github.searls.jasmine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.antlr.grammar.v3.ANTLRParser.throwsSpec_return;
import net.awired.jscoverage.instrumentation.JsInstrumentationException;
import net.awired.jscoverage.instrumentation.JsInstrumentedSource;
import net.awired.jscoverage.instrumentation.JsInstrumentor;

import com.github.searls.jasmine.coffee.CompilesAllCoffeeInDirectory;
import com.github.searls.jasmine.io.DirectoryCopier;
import com.github.searls.jasmine.io.FileUtilsWrapper;
import com.github.searls.jasmine.io.ScansDirectory;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

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
        private JsInstrumentor jsInstrumentor = new JsInstrumentor();
        private ScansDirectory scansDirectory = new ScansDirectory();
        private FileUtilsWrapper fileUtilsWrapper = new FileUtilsWrapper();

	public void run() throws IOException {
		getLog().info("Processing JavaScript Sources");
		if (sources.getDirectory().exists()) {
			File destination = new File(jasmineTargetDir, srcDirectoryName);
			directoryCopier.copyDirectory(sources.getDirectory(), destination);
			processInstrumentSources(jasmineTargetDir, destination);
			compilesAllCoffeeInDirectory.compile(destination);
		} else {
			getLog().warn(MISSING_DIR_WARNING);
		}
	}

    private void processInstrumentSources(File jasmineTargetDir, File destination) {
        if (coverage) {
            List<String> scan = scansDirectory.scan(destination, ScansDirectory.DEFAULT_INCLUDES,
                    new ArrayList<String>());
            for (String file : scan) {
                try {
                    JsInstrumentedSource instrument = jsInstrumentor.instrument(file,
                            fileUtilsWrapper.readFileToString(new File(destination, file)));
                    File instrumentedfile = new File(jasmineTargetDir, instrumentedDirectoryName + "/" + file);
                    fileUtilsWrapper.forceMkdir(instrumentedfile.getParentFile());
                    fileUtilsWrapper.writeStringToFile(instrumentedfile, instrument.getIntrumentedSource(), "UTF-8");
                } catch (FileNotFoundException e) {
                    throw new IllegalStateException("cannot find source code to instrument", e);
                } catch (Exception e) {
                    throw new IllegalStateException("cannot instrument source code", e);
                }
            }
        }
    }
	
}
