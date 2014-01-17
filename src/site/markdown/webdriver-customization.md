WebDriver Customization
=======================
The jamine-maven-plugin ships with [HtmlUnitDriver](https://code.google.com/p/selenium/wiki/HtmlUnitDriver) by default but it is also possible to change to any other [WebDriver](http://docs.seleniumhq.org/projects/webdriver/) implementation and configure it with [Capabilities](http://selenium.googlecode.com/git/docs/api/java/org/openqa/selenium/Capabilities.html).

Changing the implementation
---------------------------
The `webDriverClassName` parameter allows you to specify the WebDriver implementation you would like to use in place of HtmlUnitDriver. The WebDriver implementation must either have a constructor that excepts no arguments or a constructor that accepts a single [Capabilities](http://selenium.googlecode.com/git/docs/api/java/org/openqa/selenium/Capabilities.html) argument. 

Here is an example using FirefoxDriver:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>com.github.searls</groupId>
      <artifactId>jasmine-maven-plugin</artifactId>
      <version>${jasmine-plugin-version}</version>
      <executions>
        <execution>
          <goals>
            <goal>test</goal>
          </goals>
          <configuration>
            <webDriverClassName>org.openqa.selenium.firefox.FirefoxDriver</webDriverClassName>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

Setting capabilities
--------------------
You can configure your WebDriver implementation using [Capabilities](http://selenium.googlecode.com/git/docs/api/java/org/openqa/selenium/Capabilities.html) as a simple String, List, or a Map. The capabilities are configured using the `webDriverCapabilities` parameter and you may provide multiple capabilities.

* Capability as a String

``` xml
<webDriverCapabilities>
  <capability>
    <name>phantomjs.binary.path</name>
    <value>/opt/phantomjs/bin/phantomjs</value>
  </capability>
</webDriverCapabilities>
```

* Capability as a List

``` xml
<webDriverCapabilities>
  <capability>
    <name>phantomjs.cli.args</name>
    <list>
      <value>--disk-cache=true</value>
      <value>--max-disk-cache-size=256</value>
    </list>
  </capability>
</webDriverCapabilities>
```

* Capability as a Map

``` xml
<webDriverCapabilities>
  <capability>
    <name>proxy</name>
    <map>
      <httpProxy>myproxyserver.com:8000</httpProxy>
    </map>
  </capability>
</webDriverCapabilities>
```

You will need to consult with your WebDriver implementation to determine what capabilities are possible.  If a capability can not be provided as either a String, List, or Map then it is currently not supported.

Lastly, we do not test every possible WebDriver implementation with every possible permutation of capabilities so it is very possible that they will not work. Feel free to file a bug if you come across such a capability that you feel should be supported.

Examples
--------

Configuring the default HtmlUnitDriver to use a proxy:

```xml
<plugin>
  <groupId>com.github.searls</groupId>
  <artifactId>jasmine-maven-plugin</artifactId>
  <version>${jasmine-maven-plugin.version}</version>
  <executions>
    <execution>
      <goals>
        <goal>test</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <webDriverCapabilities>
      <capability>
        <name>proxy</name>
        <map>
          <httpProxy>myproxyserver.com:8000</httpProxy>
        </map>
      </capability>
    </webDriverCapabilities>
  </configuration>
</plugin>
```

Configuring PhantomJSDriver with custom binary path and caching settings:

```xml
<plugin>
  <groupId>com.github.searls</groupId>
  <artifactId>jasmine-maven-plugin</artifactId>
  <version>${jasmine-maven-plugin.version}</version>
  <executions>
    <execution>
      <goals>
        <goal>test</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <webDriverClassName>org.openqa.selenium.phantomjs.PhantomJSDriver</webDriverClassName>
    <webDriverCapabilities>
      <capability>
        <name>phantomjs.binary.path</name>
        <value>/opt/phantomjs/bin/phantomjs</value>
      </capability>
      <capability>
        <name>phantomjs.cli.args</name>
        <list>
          <value>--disk-cache=true</value>
          <value>--max-disk-cache-size=256</value>
        </list>
      </capability>
    </webDriverCapabilities>
  </configuration>
</plugin>
```
