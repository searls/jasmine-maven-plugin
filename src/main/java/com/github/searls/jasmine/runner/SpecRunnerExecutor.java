package com.github.searls.jasmine.runner;

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
	
	public static final String BUILD_REPORT_JS = "/buildReport.js";
	public static final String BUILD_CONCLUSION_JS = "/buildConclusion.js";
	public static final String CREATE_JUNIT_XML = "/createJunitXml.js";

	private static final long MAX_EXECUTION_MILLIS = 300000; //5 minutes - TODO make this configurable
	
	private IOUtilsWrapper ioUtilsWrapper = new IOUtilsWrapper();
	private FileUtilsWrapper fileUtilsWrapper = new FileUtilsWrapper();
	
	public JasmineResult execute(URL runnerUrl, File junitXmlReport, String browserVersion) {
		try {
			BrowserVersion htmlUnitBrowserVersion = (BrowserVersion) BrowserVersion.class.getField(browserVersion).get(BrowserVersion.class);
			WebClient webClient = new WebClient(htmlUnitBrowserVersion);
			webClient.setJavaScriptEnabled(true);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			quietIncorrectnessListener(webClient);
			
		    HtmlPage page = webClient.getPage(runnerUrl);
		    waitForRunnerToFinish(page);
		    JasmineResult jasmineResult = new JasmineResult();
		    jasmineResult.setDescription(buildRunnerDescription(page));
		    jasmineResult.setDetails(buildReport(page));
		    fileUtilsWrapper.writeStringToFile(junitXmlReport, buildJunitXmlReport(page), "UTF-8");
		    webClient.closeAllWindows();
	    
		    return jasmineResult;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	private String buildReport(HtmlPage page) throws IOException {
		ScriptResult report = page.executeJavaScript(ioUtilsWrapper.toString(BUILD_REPORT_JS));
		return report.getJavaScriptResult().toString();
	}

	private String buildRunnerDescription(HtmlPage page) throws IOException {
		ScriptResult description = page.executeJavaScript(ioUtilsWrapper.toString(BUILD_CONCLUSION_JS));
		return description.getJavaScriptResult().toString();
	}

	private String buildJunitXmlReport(HtmlPage page) throws IOException {
		ScriptResult junitReport = page.executeJavaScript(ioUtilsWrapper.toString(CREATE_JUNIT_XML) + "junitXmlReporter.report(reporter);"); 
		return junitReport.getJavaScriptResult().toString();
	}

	private void waitForRunnerToFinish(HtmlPage page) throws InterruptedException {		
		page.getWebClient().waitForBackgroundJavaScript(5000);
		int waitInMillis = 500;
		for (int i = 0; i < MAX_EXECUTION_MILLIS/waitInMillis; i++) {
			if(executionFinished(page)) {
				return;
			} else {
        		synchronized (page) {
					page.wait(waitInMillis);
        		}
            }
        }
		if(!executionFinished(page)) {
			throw new IllegalStateException("Attempted to wait for the test to complete processing over the course of "+(MAX_EXECUTION_MILLIS/1000)+" seconds," +
					"but it still appears to be running. Aborting test execution.");
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
