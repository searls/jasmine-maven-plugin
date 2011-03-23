package com.github.searls.jasmine.runner;

import org.apache.maven.plugin.logging.Log;
import java.io.File;
import java.io.IOException;
import java.net.URL;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.searls.jasmine.io.FileUtilsWrapper;
import com.github.searls.jasmine.io.IOUtilsWrapper;
import com.github.searls.jasmine.model.JasmineResult;

public class SpecRunnerExecutor {
	
	public static final String BUILD_REPORT_JS = "/lib/buildReport.js";
	public static final String CREATE_JUNIT_XML = "/lib/createJunitXml.js";

	private IOUtilsWrapper ioUtilsWrapper = new IOUtilsWrapper();
	private FileUtilsWrapper fileUtilsWrapper = new FileUtilsWrapper();
	
	public JasmineResult execute(URL runnerUrl, File junitXmlReport, String browserVersion, int timeout, boolean debug, Log log, String format) {
		try {
			//TODO - this class has numerous reasons to change: configuring a web client and running specs. extract at least one class
			BrowserVersion htmlUnitBrowserVersion = (BrowserVersion) BrowserVersion.class.getField(browserVersion).get(BrowserVersion.class);
			WebClient webClient = new WebClient(htmlUnitBrowserVersion);
			webClient.setJavaScriptEnabled(true);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());			
			if(!debug) {
				quietIncorrectnessListener(webClient);
			}
			
		    HtmlPage page = webClient.getPage(runnerUrl);
		    waitForRunnerToFinish(page, timeout, debug, log);
		    JasmineResult jasmineResult = new JasmineResult();
		    jasmineResult.setDetails(buildReport(page,format));
		    fileUtilsWrapper.writeStringToFile(junitXmlReport, buildJunitXmlReport(page,debug), "UTF-8");
		    webClient.closeAllWindows();
	    
		    return jasmineResult;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String buildReport(HtmlPage page, String format) throws IOException {
		ScriptResult report = page.executeJavaScript(
				ioUtilsWrapper.toString(BUILD_REPORT_JS) + 
				"jasmineMavenPlugin.printReport(window.reporter,{format:'"+format+"'});");
		return report.getJavaScriptResult().toString();
	}

	private String buildJunitXmlReport(HtmlPage page, boolean debug) throws IOException {
		ScriptResult junitReport = page.executeJavaScript(
				ioUtilsWrapper.toString(CREATE_JUNIT_XML) + 
				"junitXmlReporter.report(reporter,"+debug+");"); 
		return junitReport.getJavaScriptResult().toString();
	}

	private void waitForRunnerToFinish(HtmlPage page, int timeout, boolean debug, Log log) throws InterruptedException {
		page.getWebClient().waitForBackgroundJavaScript(5000);
		for (int i = 0; i < timeout; i++) {
			if (executionFinished(page)) {
				return;
			} else {
				synchronized (page) {
					page.wait(1000);
				}
			}
		}
		if (!executionFinished(page)) {
			handleTimeout(timeout, debug, log);
		}
	}

	private void handleTimeout(int timeout, boolean debug, Log log) {
		log.warn("Attempted to wait for your specs to finish processing over the course of " +
					timeout + 
					" seconds, but it still appears to be running.");
		if(debug) {
			log.warn("Debug mode: will attempt to parse the incomplete spec runner results");
		} else {
			throw new IllegalStateException("Timeout occurred. Aborting execution of specs. (Try configuring 'debug' to 'true' for more details.)");
		}
	}

	private Boolean executionFinished(HtmlPage page) {
		ScriptResult result = page.executeJavaScript("reporter.finished");
		return (Boolean) result .getJavaScriptResult();
	}

	private void quietIncorrectnessListener(WebClient webClient) {
		//Disables stuff like this "com.gargoylesoftware.htmlunit.IncorrectnessListenerImpl notify WARNING: Obsolete content type encountered: 'text/javascript'."
		webClient.setIncorrectnessListener(new IncorrectnessListener() {
			public void notify(String arg0, Object arg1) {}
		});
	}
}
