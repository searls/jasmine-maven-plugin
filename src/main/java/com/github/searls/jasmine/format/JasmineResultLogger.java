package com.github.searls.jasmine.format;

import org.apache.maven.plugin.logging.Log;

import com.github.searls.jasmine.model.JasmineResult;


public class JasmineResultLogger {

  public static final String HEADER="\n"+
    "-------------------------------------------------------\n"+
    " J A S M I N E   S P E C S\n"+
    "-------------------------------------------------------";

  private Log log;

  public void setLog(Log log) {
    this.log = log;
  }

  public void log(JasmineResult result) {
    log.info(HEADER);
    log.info(result.getDetails());
  }

}
