jasmine-maven-plugin
====================
**A Maven Plugin for processing JavaScript sources, specs, and executing Jasmine**

### Good news everyone! jasmine-maven-plugin is now hosted in Maven Central!

If you want to use Maven and test-drive JavaScript, this is the plugin for you!

* Generates two HTML test runners: one for test-driving locally in your browser, and one to run as part of the build
* Continuous integration with no added configuration: because the plugin's `test` goal runs headlessly (thanks HtmlUnit!), your CI system won't need any additional configuration. Your build will fail as soon as your JavaScript tests do.
* Builds JUnit XML: your CI reporting can incorporate each Jasmine spec alongside any reports of your existing xUnit tests

Option A: Start from the archetype
----------------------------------

From the command line, generate a new project using the [jasmine-archetype](http://github.com/searls/jasmine-archetype). 
See the [jasmine-archetype](http://github.com/searls/jasmine-archetype) project page for more information. Otherwise, just execute this command to get started:

    mvn archetype:generate \
    -DarchetypeRepository=http://searls-maven-repository.googlecode.com/svn/trunk/snapshots \
    -DarchetypeGroupId=com.github.searls \
    -DarchetypeArtifactId=jasmine-archetype \
    -DarchetypeVersion=1.0.1-SNAPSHOT \
    -DgroupId=com.acme \
    -DartifactId=my-jasmine-project \
    -Dversion=0.0.1-SNAPSHOT    

Option B: Add to your existing project
--------------------------------------

Add the relevant plugin and repositories entries to your project's `pom.xml`.

    <project>
      <build>
        ...
        <plugins>
          ...
          <plugin>
            <groupId>com.github.searls</groupId>
            <artifactId>jasmine-maven-plugin</artifactId>
            <version>1.0.1-SNAPSHOT</version>
            <executions>
              <execution>
                <goals>
                  <goal>generateManualRunner</goal>
                  <goal>resources</goal>
                  <goal>testResources</goal>
                  <goal>test</goal>
                  <goal>preparePackage</goal>
                </goals>
              </execution>
            </executions>
          </plugin>
          ...
        </plugins>
      </build>
      ...
    </project>

Building your project with Jasmine
------------------

    mvn package

Executing any Maven lifecycle phase after prepare-package will show off everything this plugin has to offer. However, the results will only be useful once you've added some JavaScript and specs. Details follow:

###src/main/javascript 
By default, the plugin expects to find your JavaScript sources  (i.e. `ninja.js`) and dependencies (i.e. `lib/prototype.js`) in `src/main/javascript`. However, for most
existing projects, it will make more sense to specify where your JS sources are in `src/main/webapp` and forego the packageResource goal (see "Supporting WTP" below for an example).

###src/test/javascript 
Store your Jasmine specs (i.e. `ninjaSpec.js`) in `src/test/javascript`. No need to create an HTML spec runner, one will be generated and executed for you by the **jasmine:test** goal!
    
Example test Output
-------------------
jasmine-maven-plugin behaves just like maven-surefire-plugin and will fail the build on spec failures (unless haltOnFailure is set to false). 

An example of some failing output follows:

    -------------------------------------------------------
     J A S M I N E   T E S T S
    -------------------------------------------------------
    [INFO] describe FailSpec <<< FAILURE!
    [INFO]   describe NestedFail <<< FAILURE!
    [INFO]     it should fail deeply <<< FAILURE!
    [INFO]       * Expected true to be false.
    [INFO]   it should fail <<< FAILURE!
    [INFO]     * Expected true to be false.
    [INFO] describe HelloWorld <<< FAILURE!
    [INFO]   it should say hello
    [INFO]   it should say goodbye <<< FAILURE!
    [INFO]     * Expected 'Hello, World' to be 'Goodbye, World'.
    [INFO]   it should fail <<< FAILURE!
    [INFO]     * Expected 5 to be 6.
    [INFO] 
    Results:
    
    5 specs, 4 failures in 0.144s
    
    [INFO] ------------------------------------------------------------------------
    [ERROR] BUILD FAILURE
    [INFO] ------------------------------------------------------------------------
    [INFO] There were test failures.
    [INFO] ------------------------------------------------------------------------

Usage Notes
-----------
### Project layout
The jasmine-maven-plugin presumes a default project directory layout. If this layout doesn't suit your project, fear not, as it's entirely customizable. In adition to everything documented here,
you can check the documented source of the [base Mojo class](https://github.com/searls/jasmine-maven-plugin/blob/master/src/main/java/searls/jasmine/AbstractJasmineMojo.java) to see which properties have been
parameterized.

An included example project (in [src/test/resources/examples/jasmine-webapp-example](http://github.com/searls/jasmine-maven-plugin/tree/master/src/test/resources/examples/jasmine-webapp-example/)) is laid out like this: 

    |-- pom.xml
    |-- src
    |   |-- main
    |   |   |-- javascript
    |   |   |   |-- HelloWorld.js
    |   |   |   `-- vendor
    |   |   |       `-- jquery-1.4.2.min.js    
    |   |   `-- webapp
    |   |       `-- index.html
    |   `-- test
    |       `-- javascript
    |           `-- HelloWorldSpec.js
    `-- target
        |-- classes
        |-- jasmine
        |   |-- ManualSpecRunner.html
        |   |-- SpecRunner.html
        |   |-- spec
        |   |   `-- HelloWorldSpec.js
        |   `-- src
        |       |-- HelloWorld.js
        |       `-- vendor
        |           `-- jquery-1.4.2.min.js
        |-- jasmine-webapp-example
        |   |-- index.html
        |   `-- js
        |       |-- HelloWorld.js
        |       `-- vendor
        |           `-- jquery-1.4.2.min.js
        `-- jasmine-webapp-example.war

As seen above, by default, the plugin looks for JavaScript placed in `src/main/javascript`, while test specs are each in `src/test/javascript`. The plugin supports nested directories and will maintain your directory structure as it processes the source directories.

### Goals

####jasmine:resources
This goal binds to the process-resources phase and copies the `src/main/javascript` directory into `target/jasmine/src`. 
It can be changed by configuring a parameter named `jsSrcDir` in the plugin execution section of the POM.

####jasmine:testResources
This goal binds to the process-test-resources phase and copies the `src/test/javascript` directory into `target/jasmine/spec`. 
It can be changed by configuring a parameter named `jsTestSrcDir` in the plugin execution section of the POM.

####jasmine:test
This goal binds to the test phase and generates a Jasmine runner file in `target/jasmine/SpecRunner.html` based on the sources processed by the previous two goals and Jasmine's own dependencies. 
It will respect the `skipTests` property, and will not halt processing if `haltOnFailure` is set to false.

####jasmine:preparePackage 
This goal binds to the prepare-package phase and copies the production JavaScript sources from `target/jasmine/src` to `/js` within the package directory (e.g. `target/your-webapp/js`). 
The sub-path can be cleared or changed by setting the `packageJavaScriptPath` property

####jasmine:generateManualRunner
This goal binds to the generate-test-sources phase and will generate an extra spec runner HTML (named `ManualSpecRunner.html` by default) in the jasmine target directory (`target/jasmine`). 
This way, you can easily run your specs in the browser as you develop them, while still leaning on the plugin to keep the HTML up-to-date for you. Note that this HTML file is separate from 
the one generated during **jasmine:test**, because it points to the source directories directly.

When using the manual runner in a browser, be careful to edit your source & specs in the project's `src` directory, even though the runner itself runs in `target`! 

### Supporting WTP (Leaving your JavaScript sources in your `webapp` directory)
You can run the plugin with all five goals or fewer, if you choose. For instance, if you run your application in Eclipse WTP and you want to keep your production 
JavaScript in `src/main/webapp` to facilitate easier iterative development, you could skip the preparePackage goal and configure the `jsSrcDir` property to point 
at `src/main/webapp/[your-js-directory]` instead. 

Here's an example POM snippet:

    <execution>
      <goals>
        <goal>resources</goal>
        <goal>testResources</goal>
        <goal>test</goal>
      </goals>
      <configuration>
        <jsSrcDir>${project.basedir}/src/main/webapp/js</jsSrcDir>
      </configuration>
    </execution>

### Enforcing the order in which JavaScript files are loaded 
You can configure the plugin to load a list of JavaScript sources before any others. This is particularly useful when your scripts or specs must be loaded in a particular order
to work correctly. 

So if you wanted to make sure jQuery was loaded before your application's scripts, and that the terrific [jasmine-jquery](https://github.com/velesin/jasmine-jquery) was 
loaded before your specs, and you wanted to load [Prototype](http://www.prototypejs.org/) from Google CDN (which isn't even stored in your project), your POM would look like this: 

    <configuration>
      ...
      <preloadSources>
        <!-- Production dependencies that need to come first -->
        <source>vendor/jquery.js</source>
        <source>vendor/jquery-ui.js</source>
        
        <!-- Also supports test dependencies that need to come before specs -->
        <source>jasmine-jquery.js</source>
        
        <!-- Even supports remote resources and arbitrary protocols -->
        <source>https://ajax.googleapis.com/ajax/libs/prototype/1.7.0.0/prototype.js</source>
      </preloadSources>				
    </configuration>
    
As demonstrated above, `preloadSources` will attempt to resolve each specified source in this order (before placing it in an HTML `script` tag):

1. As a file that exists relative to the `jsSrcDir`
2. As a file that  exists relative to the `jsTestSrcDir`
3. Exactly as entered into the POM (e.g. "http://../script.js", "ftp://blah.js", "/path/to/my-other-project/../script.js", etc.)

### Creating a custom SpecRunner HTML template

Sometimes the plugin's generated HTML runners might not fit your project's needs (perhaps you want to incorporate JSLint/JSCoverage into the runner or simply work around a bug in the plugin). 

While you're encouraged to [create an issue](https://github.com/searls/jasmine-maven-plugin/issues) when you 
find a way in which the plugin is lacking, one approach to unblocking yourself immediately is to override the plugin's own SpecRunner HTML template. 

To use a custom runner template:

1. Create a new empty file in your project (I'd recommend somewhere in `src/test/resources`)
2. While [eyeballing the plugin's default template](https://github.com/searls/jasmine-maven-plugin/blob/master/src/main/resources/jasmine-templates/SpecRunner.htmltemplate), write your custom template file.
3. Configure jasmine-maven-plugin to use your custom runner template.

The configuration name is `customRunnerTemplate` and would be configured in the POM like so:

    <configuration>
      ...
      <customRunnerTemplate>${project.basedir}/src/test/resources/path/to/my_spec_runner.template</customRunnerTemplate>				
    </configuration>
    

### JUnit XML Reports

The plugin's `test` goal will output the test results in a JUnit text XML report, located in `target/jasmine/TEST-jasmine.xml`. The implementation attempts to satisfy the most middle-of-the-road consensus as to what the schema-less XML report "[should](http://stackoverflow.com/questions/442556/spec-for-junit-xml-output)" look like.

As an example, to integrate the report into a Hudson job (note that it must be a **freestyle** job), select "Publish JUnit test result report" among the available "Post-build Actions" and include a file pattern like "\*\*/jasmine/TEST\*.xml". Once included, your jasmine specs will be counted and interactive in the same way your other tests are!

### Specifying which Browser to execute Jasmine specs with

By default, the plugin will execute the project's specs using HtmlUnit's "FIREFOX_3" [BrowserVersion](http://htmlunit.sourceforge.net/apidocs/com/gargoylesoftware/htmlunit/BrowserVersion.html).
If you'd like to execute your specs against a different one of its profiles, you can specify it in the plugin's configuration in your POM.
HtmlUnit currently only offers a few flavors of FireFox and IE (see [its JavaDoc for the exact names]((http://htmlunit.sourceforge.net/apidocs/com/gargoylesoftware/htmlunit/BrowserVersion.html))),
but here is an example configuration specifying that specs should be executed against HtmlUnit's IE6 profile:

    <configuration>
      ...
      <browserVersion>INTERNET_EXPLORER_6</browserVersion>				
    </configuration>

### Current Version Info

The plugin's version numbering will mirror the version of Jasmine that backs it. The latest version of the plugin points to Jasmine 1.0.1, so its version number is 1.0.1-SNAPSHOT. 
If you need a non-snapshot release (say, to satisify the maven-release-plugin), you may use **1.0.1-beta-5**.  

## Maintainers
* [Justin Searls](http://about.emw/searls), [Pillar Technology](http://pillartechnology.com)

## Contributions
Pull requests are, of course, very welcome! A few todos, if anyone is interested in tackling them:

* JSLint and JSCoverage integration
* Parse & format ignored tests (currently only passing & failing tests are parsed)
* A facility that automatically executes the other goals if only `test` or `preparePackage` is configured to run.

## Acknowledgments
* Thanks to Pivotal Labs for authoring and publishing [Jasmine](http://github.com/pivotal/jasmine)
* Thanks to christian.nelson and sivoh1, owners of the [javascript-test-maven-plugin](http://code.google.com/p/javascript-test-maven-plugin/) project, which provided a similar implementation from which to glean several valuable lessons.
