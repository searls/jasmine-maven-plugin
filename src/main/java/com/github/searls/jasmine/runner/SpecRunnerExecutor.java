package com.github.searls.jasmine.runner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.awired.jscoverage.result.JsRunResult;
import net.awired.jscoverage.result.LcovWriter;
import org.antlr.grammar.v3.ANTLRParser.throwsSpec_return;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.github.searls.jasmine.io.FileUtilsWrapper;
import com.github.searls.jasmine.io.IOUtilsWrapper;
import com.github.searls.jasmine.model.JasmineResult;
import com.google.common.base.Predicate;

public class SpecRunnerExecutor {
	
	public static final String BUILD_REPORT_JS = "/lib/buildReport.js";
	public static final String CREATE_JUNIT_XML = "/lib/createJunitXml.js";

	private IOUtilsWrapper ioUtilsWrapper = new IOUtilsWrapper();
	private FileUtilsWrapper fileUtilsWrapper = new FileUtilsWrapper();
	private LcovWriter lcovWriter = new LcovWriter();
	 String jsGetCoverageScript = "JSCOV.storeCurrentRunResult();" + "return JSON.stringify(JSCOV.getStoredRunResult());";
	
	public JasmineResult execute(URL runnerUrl, File junitXmlReport, WebDriver driver, int timeout, boolean debug, Log log, String format, boolean coverage, File coverageReportFile) {
		try {
			if (!(driver instanceof JavascriptExecutor)) {
				throw new RuntimeException("The provided web driver can't execute JavaScript: " + driver.getClass());
			}
			JavascriptExecutor executor = (JavascriptExecutor) driver;
		    driver.get(runnerUrl.toString());
		    waitForRunnerToFinish(driver, timeout, debug, log);
		    JasmineResult jasmineResult = new JasmineResult();
		    jasmineResult.setDetails(buildReport(executor,format));
		    fileUtilsWrapper.writeStringToFile(junitXmlReport, buildJunitXmlReport(executor,debug), "UTF-8");
                    if (coverage) {
                        JsRunResult coverageReport = buildCoverageReport(executor);
                        lcovWriter.write(coverageReportFile, coverageReport);
                    }

		    driver.quit();
	    
		    return jasmineResult;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String buildReport(JavascriptExecutor driver, String format) throws IOException {
		String script = 
				ioUtilsWrapper.toString(BUILD_REPORT_JS) + 
				"return jasmineMavenPlugin.printReport(window.reporter,{format:'"+format+"'});";
		Object report = driver.executeScript(script);
		return report.toString();
	}

	private String buildJunitXmlReport(JavascriptExecutor driver, boolean debug) throws IOException {
		Object junitReport = driver.executeScript(
				ioUtilsWrapper.toString(CREATE_JUNIT_XML) + 
				"return junitXmlReporter.report(reporter,"+debug+");"); 
		return junitReport.toString();
	}

	private void waitForRunnerToFinish(final WebDriver driver, int timeout, boolean debug, Log log) throws InterruptedException {
		final JavascriptExecutor executor = (JavascriptExecutor) driver;
		new WebDriverWait(driver, timeout, 1000).until(new Predicate<WebDriver>() {
			public boolean apply(WebDriver input) {
				return executionFinished(executor);
			}
		});
		
		if (!executionFinished(executor)) {
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

	private Boolean executionFinished(JavascriptExecutor driver) {
		return (Boolean) driver.executeScript("return reporter.finished");
	}
	
	
	
    private JsRunResult buildCoverageReport(JavascriptExecutor driver) {
        Object junitReport = driver.executeScript(jsGetCoverageScript);
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<JsRunResult>> typeRef = new TypeReference<List<JsRunResult>>() {
        };
        try {
            List<JsRunResult> runResults = mapper.readValue((String) junitReport, typeRef);
            if (runResults.size() == 0) {
                throw new IllegalStateException("No coverage report found ");
            }
            return runResults.get(0);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot parse coverage result", e);
        }
    }

}
