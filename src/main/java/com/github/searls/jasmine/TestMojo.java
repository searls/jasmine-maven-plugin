package com.github.searls.jasmine;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.jetty.server.Server;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.github.searls.jasmine.format.JasmineResultLogger;
import com.github.searls.jasmine.model.JasmineResult;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.runner.SpecRunnerExecutor;


/**
 * @component
 * @goal test
 * @phase test
 * 
 */
public class TestMojo extends AbstractServerMojo {

	@Override
	protected void executeJasmine(Server server) throws Exception {
		if(!this.skipTests) {
			try {
				server.start();
				this.getLog().info("Executing Jasmine Specs");
				JasmineResult result = this.executeSpecs(new URL("http://localhost:"+this.getPort()));
				this.logResults(result);
				this.throwAnySpecFailures(result);
			} finally {
				server.stop();
			}
		} else {
			this.getLog().info("Skipping Jasmine Specs");
		}
	}

	@Override
	protected ReporterType getReporterType() {
		return ReporterType.JsApiReporter;
	}

	@Override
	protected String getSpecRunnerFilename() {
		return this.specRunnerHtmlFileName;
	}

	private JasmineResult executeSpecs(URL runner) throws MalformedURLException {
		WebDriver driver = this.createDriver();
		JasmineResult result = new SpecRunnerExecutor().execute(
				runner,
				new File(this.jasmineTargetDir,this.junitXmlReportFileName),
				driver,
				this.timeout, this.debug, this.getLog(), this.format);
		return result;
	}

	private WebDriver createDriver() {
		if (!HtmlUnitDriver.class.getName().equals(this.webDriverClassName)) {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends WebDriver> klass = (Class<? extends WebDriver>) Class.forName(this.webDriverClassName);
				Constructor<? extends WebDriver> ctor = klass.getConstructor();
				return ctor.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Couldn't instantiate webDriverClassName", e);
			}
		}

		// We have extra configuration to do to the HtmlUnitDriver
		BrowserVersion htmlUnitBrowserVersion;
		try {
			htmlUnitBrowserVersion = (BrowserVersion) BrowserVersion.class.getField(this.browserVersion).get(BrowserVersion.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		HtmlUnitDriver driver = new HtmlUnitDriver(htmlUnitBrowserVersion) {
			@Override
			protected WebClient modifyWebClient(WebClient client) {
				client.setAjaxController(new NicelyResynchronizingAjaxController());

				//Disables stuff like this "com.gargoylesoftware.htmlunit.IncorrectnessListenerImpl notify WARNING: Obsolete content type encountered: 'text/javascript'."
				if (!TestMojo.this.debug)
					client.setIncorrectnessListener(new IncorrectnessListener() {
						@Override
						public void notify(String arg0, Object arg1) {}
					});

				return client;
			};
		};
		
		// setup proxy if specified
		String proxyHost = System.getProperty("http.proxyHost");
		String proxyPort = System.getProperty("http.proxyPort");
		if (proxyHost != null && proxyPort != null) {
			getLog().debug("Setting proxy " + proxyHost + ":" + proxyPort);
			driver.setProxy(proxyHost, Integer.parseInt(proxyPort));
		}
		
		driver.setJavascriptEnabled(true);
		return driver;
	}


	private void logResults(JasmineResult result) {
		JasmineResultLogger resultLogger = new JasmineResultLogger();
		resultLogger.setLog(this.getLog());
		resultLogger.log(result);
	}

	private void throwAnySpecFailures(JasmineResult result) throws MojoFailureException {
		if(this.haltOnFailure && !result.didPass()) {
			throw new MojoFailureException("There were Jasmine spec failures.");
		}
	}
}
