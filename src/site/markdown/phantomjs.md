Using with PhantomJS
===================================
Starting with version `1.3.1.1` it is possible to configure the jasmine-maven-plugin to use [PhantomJS](http://phantomjs.org) instead of [HtmlUnit](http://htmlunit.sourceforge.net/) to execute your specs.

Here is an example configuration:

```
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
            <webDriverClassName>org.openqa.selenium.phantomjs.PhantomJSDriver</webDriverClassName>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```
The above configuration assumes that the `phantomjs` binary is on your systems `PATH`. If you would prefer, you can also specify the location of the binary using a configuration like this:

```
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
            <webDriverClassName>org.openqa.selenium.phantomjs.PhantomJSDriver</webDriverClassName>
            <webDriverCapabilities>
              <phantomjs.binary.path>/opt/phantomjs/bin/phantomjs</phantomjs.binary.path>
            </webDriverCapabilities>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

For more information on configuration options for PhantomJSDriver see its [documentation](https://github.com/detro/ghostdriver).
