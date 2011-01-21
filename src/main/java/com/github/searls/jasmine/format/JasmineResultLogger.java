package com.github.searls.jasmine.format;

import org.apache.maven.plugin.logging.Log;

import com.github.searls.jasmine.model.JasmineResult;


public class JasmineResultLogger {

	public static final String HEADER="\n"+
		"-------------------------------------------------------\n"+
		" J A S M I N E   T E S T S\n"+
		"-------------------------------------------------------";
	public static final String FAIL_APPENDAGE = " <<< FAILURE!";
	public static final String INDENT = "  ";
	
	private Log log;

	public void setLog(Log log) {
		this.log = log;
	}

	public void log(JasmineResult result) {
		log.info(HEADER);
		
		log.info(result.getDetails());

		log.info("\nResults:\n\n"+result.getDescription()+"\n");		
	}

}
