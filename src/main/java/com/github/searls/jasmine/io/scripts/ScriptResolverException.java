package com.github.searls.jasmine.io.scripts;

public class ScriptResolverException extends Exception {

  private static final long serialVersionUID = 1L;

  public ScriptResolverException() {
    super();
  }

  public ScriptResolverException(String message) {
    super(message);
  }

  public ScriptResolverException(String message, Throwable cause) {
    super(message,cause);
  }

  public ScriptResolverException(Throwable cause) {
    super(cause);
  }
}
