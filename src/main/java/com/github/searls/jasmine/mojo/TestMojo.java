package com.github.searls.jasmine.mojo;

import java.io.File;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.eclipse.jetty.server.Server;
import org.openqa.selenium.WebDriver;

import com.github.searls.jasmine.NullLog;
import com.github.searls.jasmine.driver.WebDriverFactory;
import com.github.searls.jasmine.format.JasmineResultLogger;
import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.model.JasmineResult;
import com.github.searls.jasmine.runner.CreatesRunner;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.runner.SpecRunnerExecutor;
import com.github.searls.jasmine.server.ResourceHandlerConfigurator;
import com.github.searls.jasmine.server.ServerManager;

/**
 * Execute specs using Selenium Web Driver. Uses HtmlUnitDriver for head-less execution by default.
 */
@Mojo(name="test",defaultPhase=LifecyclePhase.TEST,requiresDependencyResolution = ResolutionScope.TEST)
public class TestMojo extends AbstractJasmineMojo {

  private final RelativizesFilePaths relativizesFilePaths;

  public TestMojo() {
    this.relativizesFilePaths = new RelativizesFilePaths();
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if(!this.isSkipTests()) {
      super.execute();
    } else {
      this.getLog().info("Skipping Jasmine Specs");
    }
  }

  @Override
  public void run() throws Exception {
    ServerManager serverManager = this.getServerManager();
    try {
      int port = serverManager.start();
      setPortProperty(port);
      
      this.serverHostname = processServerHostName(this.serverHostname, this.remoteWebDriverUrl);
      
      URL runner = new URL(uriScheme+"://" + serverHostname + ":" + port);
      
      this.getLog().info(String.format("Remote selenium server: '%s'", this.remoteWebDriverUrl));
      this.getLog().info(String.format("Will fetch specs from: '%s'", runner));

      this.getLog().info("Executing Jasmine Specs");
      
      JasmineResult result = this.executeSpecs(runner);
      this.logResults(result);
      this.throwAnySpecFailures(result);
    } finally {
      if (!keepServerAlive) {
        serverManager.stop();
      }
    }
  }

  /**
   * 
   * @param serverHostname    user-provided hostname
   * @param remoteWebDriverUrl
   * 
   * @return
   */
  String processServerHostName(String serverHostname, String remoteWebDriverUrl) {
    
    final String IP       = "IP";
    final String HOSTNAME = "HOSTNAME";
    
    if (remoteWebDriverUrl != null)
    {
      if (serverHostname == null || serverHostname.equals(IP))
      {
        serverHostname = getIP();
      }
      else if (serverHostname.equals(HOSTNAME))
      {
        serverHostname = getHostName();
      }
      
      return serverHostname;
    }
    else
    {
      return serverHostname != null ? serverHostname
                                    : "localhost";
    }
  }

  private ServerManager getServerManager() throws MojoExecutionException {
    Log log = this.debug ? this.getLog() : new NullLog();

    CreatesRunner createsRunner = new CreatesRunner(
        this,
        log,
        this.specRunnerHtmlFileName,
        ReporterType.JsApiReporter);

    ResourceHandlerConfigurator configurator = new ResourceHandlerConfigurator(
        this,
        this.relativizesFilePaths,
        createsRunner);

    return new ServerManager(new Server(), getConnector(), configurator);
  }

  private void setPortProperty(int port) {
    this.mavenProject.getProperties().setProperty("jasmine.serverPort", String.valueOf(port));
  }

  private JasmineResult executeSpecs(URL runner) throws Exception {
    WebDriver driver = this.createDriver();
    JasmineResult result = new SpecRunnerExecutor().execute(
        runner,
        new File(this.jasmineTargetDir,this.junitXmlReportFileName),
        driver,
        this.timeout, this.debug, this.getLog(), this.format);
    return result;
  }
  
  String getHostName() {
    
    try {
      
      return InetAddress.getLocalHost().getHostName();
      
    } catch (UnknownHostException e) {
      
      throw new RuntimeException(e);
    }
  }
  
  String getIP() {
    
    List<String> ipList = getIPList();
    
    if (ipList.size() > 1) {
      this.getLog().warn(String.format("More than one IP found: %s. Might pick the wrong one.", ipList));
    }
    
    return ipList.isEmpty() ? null
                             : ipList.get(0);
  }
  
  List<String> getIPList() {
    
    try {

      List<String> ipList = new ArrayList<String>();

      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

      while (interfaces.hasMoreElements()) {
        NetworkInterface current = interfaces.nextElement();

        /*
         * [...] the InetAddress API provides methods for testing for loopback, link local, site local,
         * multicast and broadcast addresses. You can use these to sort out which of the IP addresses you
         * get back is most appropriate.
         * 
         * [http://stackoverflow.com/a/9482369/1553043]
         */
        if (!current.isUp() || current.isLoopback() || current.isVirtual()) continue;

        Enumeration<InetAddress> addresses = current.getInetAddresses();

        while (addresses.hasMoreElements()) {
          InetAddress current_addr = addresses.nextElement();

          //  Filter IPv6 and loopback (again)
          if (current_addr.isLoopbackAddress() || current_addr instanceof Inet6Address) continue;

          String hostAddress = current_addr.getHostAddress();

          // System.out.println(hostAddress);

          ipList.add(hostAddress);
        }
      }

      return ipList;
    }
    catch(SocketException e) {
      throw new RuntimeException(e);
    }
  }

  private WebDriver createDriver() throws Exception {
    WebDriverFactory factory = new WebDriverFactory();
    factory.setWebDriverCapabilities(webDriverCapabilities);
    factory.setWebDriverClassName(webDriverClassName);
    factory.setRemoteWebDriverUrl(remoteWebDriverUrl);
    factory.setDebug(debug);
    factory.setBrowserVersion(browserVersion);
    return factory.createWebDriver();
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
