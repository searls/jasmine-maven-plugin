package com.github.searls.jasmine;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;

import com.github.searls.jasmine.io.scripts.TargetDirScriptResolver;
import com.github.searls.jasmine.runner.*;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.github.searls.jasmine.format.JasmineResultLogger;
import com.github.searls.jasmine.model.JasmineResult;


/**
 * @component
 * @goal test
 * @phase test
 * @execute phase="jasmine-process-test-resources"
 */
public class TestMojo extends AbstractJasmineMojo {

	public void run() throws Exception {
		if(!skipTests) {
			getLog().info("Executing Jasmine Specs");
			File runnerFile = writeSpecRunnerToOutputDirectory();
			JasmineResult result = executeSpecs(runnerFile);
			logResults(result);
			throwAnySpecFailures(result);
		} else {
			getLog().info("Skipping Jasmine Specs");
		}
	}

	private File writeSpecRunnerToOutputDirectory() throws IOException {

		SpecRunnerHtmlGenerator generator = new SpecRunnerHtmlGeneratorFactory().create(ReporterType.JsApiReporter, this, new TargetDirScriptResolver(this));

		String html = generator.generate();

		getLog().debug("Writing out Spec Runner HTML " + html + " to directory " + jasmineTargetDir);
		File runnerFile = new File(jasmineTargetDir,specRunnerHtmlFileName);
		FileUtils.writeStringToFile(runnerFile, html);
		return runnerFile;
	}

	private JasmineResult executeSpecs(File runnerFile) throws MalformedURLException {
		WebDriver driver = createDriver();
		JasmineResult result = new SpecRunnerExecutor().execute(
			runnerFile.toURI().toURL(), 
			new File(jasmineTargetDir,junitXmlReportFileName), 
			driver, 
			timeout, debug, getLog(), format);
		return result;
	}

	private WebDriver createDriver() {
		if (!HtmlUnitDriver.class.getName().equals(webDriverClassName)) {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends WebDriver> klass = (Class<? extends WebDriver>) Class.forName(webDriverClassName);
				Constructor<? extends WebDriver> ctor = klass.getConstructor();
				return ctor.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Couldn't instantiate webDriverClassName", e);
			}
		}
		
		// We have extra configuration to do to the HtmlUnitDriver
		BrowserVersion htmlUnitBrowserVersion;
		try {
			htmlUnitBrowserVersion = (BrowserVersion) BrowserVersion.class.getField(browserVersion).get(BrowserVersion.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		HtmlUnitDriver driver = new HtmlUnitDriver(htmlUnitBrowserVersion) {
			protected WebClient modifyWebClient(WebClient client) {
				client.setAjaxController(new NicelyResynchronizingAjaxController());
				
				//Disables stuff like this "com.gargoylesoftware.htmlunit.IncorrectnessListenerImpl notify WARNING: Obsolete content type encountered: 'text/javascript'."
				if (!debug) 
					client.setIncorrectnessListener(new IncorrectnessListener() {
				        public void notify(String arg0, Object arg1) {}
				});

				return client;
			};
		};
		driver.setJavascriptEnabled(true);
		return driver;
	}

	
	private void logResults(JasmineResult result) {
		JasmineResultLogger resultLogger = new JasmineResultLogger();
		resultLogger.setLog(getLog());
		resultLogger.log(result);
	}

	private void throwAnySpecFailures(JasmineResult result) throws MojoFailureException {
		if(haltOnFailure && !result.didPass()) {
			throw new MojoFailureException("There were Jasmine spec failures.");
		}
	}


}
