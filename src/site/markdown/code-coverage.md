Integrating with Saga Code Coverage
===================================
It is possible to configure your build to run Jasmine tests with the jasmine-maven-plugin and measure code coverage using the fantastic [saga-maven-plugin](http://timurstrekalov.github.com/saga/).

Note: Starting with version `1.3.1.0` of the jasmine-maven-plugin you will need to use the new `keepServerAlive` parameter to keep the web server running and you must use version `1.4.0` of the saga-maven-plugin in order to have saga consume your tests.

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
      	    <keepServerAlive>true</keepServerAlive>
          </configuration>
        </execution>
      </executions>
    </plugin>
    <plugin>
      <groupId>com.github.timurstrekalov</groupId>
      <artifactId>saga-maven-plugin</artifactId>
      <version>1.4.0</version>
      <executions>
        <execution>
          <goals>
            <goal>coverage</goal>
          </goals>
        </execution>
      </executions>
      <configuration>
        <baseDir>http://localhost:${jasmine.serverPort}</baseDir>
        <outputDir>${project.build.directory}/coverage</outputDir>
        <noInstrumentPatterns>
          <pattern>.*/spec/.*</pattern> <!-- Don't instrument specs -->
        </noInstrumentPatterns>
      </configuration>
    </plugin>
  </plugins>
</build>
```

Then running `mvn verify` should create your coverage reports in `${project.build.directory}/coverage`.
