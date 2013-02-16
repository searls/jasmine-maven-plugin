package com.github.searls.jasmine.driver;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class QuietHtmlUnitDriverTest {
  @Mock
  private WebClient mockWebClient;
  private QuietHtmlUnitDriver driver;
  private boolean debug = false;

  private void createDriver() {
    driver = new QuietHtmlUnitDriver(BrowserVersion.FIREFOX_3_6, debug);
  }

  @Test
  public void enablesJavascript() {
    createDriver();

    assertTrue(driver.isJavascriptEnabled());
  }

  private void modifyWebClient() {
    createDriver();
    driver.modifyWebClient(mockWebClient);
  }

  @Test
  public void installsNewAjaxController() throws Exception {
    modifyWebClient();

    verify(mockWebClient).setAjaxController(isA(NicelyResynchronizingAjaxController.class));
  }

  @Test
  public void overridesIncorrectnessListenerToSuppressOutput() {
    modifyWebClient();

    verify(mockWebClient).setIncorrectnessListener(isA(IncorrectnessListener.class));
  }

  @Test
  public void doesNotOverrideIncorrectnessListenerWhenDebugFlagIsSet() {
    debug = true;
    modifyWebClient();

    verify(mockWebClient, never()).setIncorrectnessListener(isA(IncorrectnessListener.class));
  }
}
