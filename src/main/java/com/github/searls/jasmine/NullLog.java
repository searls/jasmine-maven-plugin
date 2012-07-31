package com.github.searls.jasmine;

public class NullLog implements org.apache.maven.plugin.logging.Log {

  public boolean isDebugEnabled() {
    return false;
  }

  public void debug(CharSequence content) {}

  public void debug(CharSequence content, Throwable error) {}

  public void debug(Throwable error) {}

  public boolean isInfoEnabled() {
    return false;
  }

  public void info(CharSequence content) {}

  public void info(CharSequence content, Throwable error) {}

  public void info(Throwable error) {}

  public boolean isWarnEnabled() {
    return false;
  }

  public void warn(CharSequence content) {}

  public void warn(CharSequence content, Throwable error) {}

  public void warn(Throwable error) {}

  public boolean isErrorEnabled() {
    return false;
  }

  public void error(CharSequence content) {}

  public void error(CharSequence content, Throwable error) {}

  public void error(Throwable error) {}

}