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
The above configuration assumes that the `phantomjs` binary is on your systems `PATH`.

If you would prefer, you can also use [klieber's phantomjs-maven-plugin](https://github.com/klieber/phantomjs-maven-plugin) to pull down a version of phantomjs:
```
      <plugin>
        <groupId>com.github.klieber</groupId>
        <artifactId>phantomjs-maven-plugin</artifactId>
        <version>0.2.1</version>
        <executions>
          <execution>
            <goals>
              <goal>install</goal>
            </goals>
          </execution>
        </executions>
        <configuration>
          <version>1.9.2</version>
        </configuration>
      </plugin>
```
If you use com.github.klieber.phantomjs-maven-plugin edit the jasmine-maven-plugin configuration to point to the phantomjs that gets installed dynamically:

```
<webDriverCapabilities>
  <phantomjs.binary.path>${phantomjs.binary}</phantomjs.binary.path>
</webDriverCapabilities>
```

If you would prefer, you can also specify the location of the binary using a configuration like this:

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
