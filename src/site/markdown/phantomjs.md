Using with PhantomJS
====================
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
              <capability>
                <name>phantomjs.binary.path</name>
                <value>/opt/phantomjs/bin/phantomjs</value>
              </capability>
            </webDriverCapabilities>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

For more information on configuration options for PhantomJSDriver see its [documentation](https://github.com/detro/ghostdriver).

Automatically installing phantomjs
----------------------------------
One of the downsides of using phantomjs instead of HtmlUnit is it requires native binaries be present on the system you are running your build on. The [phantomjs-maven-plugin](http://klieber.github.io/phantomjs-maven-plugin) solves that problem by automatically pulling down phantomjs when needed.

Here's an example using `phantomjs-maven-plugin` with the `jasmine-maven-plugin`:

```
<build>
  <plugins>
    <plugin>
      <groupId>com.github.klieber</groupId>
      <artifactId>phantomjs-maven-plugin</artifactId>
      <version>${phantomjs-maven-plugin.version}</version>
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
    <plugin>
      <groupId>com.github.searls</groupId>
      <artifactId>jasmine-maven-plugin</artifactId>
      <version>${jasmine-maven-plugin-version}</version>
      <executions>
        <execution>
          <goals>
            <goal>test</goal>
          </goals>
          <configuration>
            <webDriverClassName>org.openqa.selenium.phantomjs.PhantomJSDriver</webDriverClassName>
            <webDriverCapabilities>
              <capability>
                <name>phantomjs.binary.path</name>
                <value>${phantomjs.binary}</value>
              </capability>
            </webDriverCapabilities>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```
