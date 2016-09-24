package com.github.searls.jasmine.format;

import com.github.searls.jasmine.model.JasmineResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JasmineResultLogger {

  private static final Logger LOGGER = LoggerFactory.getLogger(JasmineResultLogger.class);

  protected static final String HEADER = "\n" +
    "-------------------------------------------------------\n" +
    " J A S M I N E   S P E C S\n" +
    "-------------------------------------------------------";

  public void log(JasmineResult result) {
    LOGGER.info(HEADER);
    if (result != null && result.getDetails() != null) {
      LOGGER.info(result.getDetails());
    }
  }

}
