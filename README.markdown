jasmine-maven-plugin
====================
**A Maven Plugin for processing JavaScript sources, specs, and executing Jasmine**

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
    -DarchetypeVersion=1.0.2-SNAPSHOT \
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
            <version>1.0.2-beta-2</version>
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

Executing any Maven lifecycle phase after prepare-package will show off everything this plugin has to offer. However, the results will only be useful once you've added some Jasmine specs and JavaScript. Details follow:

###src/main/javascript 
By default, the plugin expects to find your JavaScript sources  (i.e. `ninja.js`) and dependencies (i.e. `lib/prototype.js`) in `src/main/javascript`. However, for most
existing projects, it will make more sense to specify where your JS sources are in (usually somewhere in `src/main/webapp`) and remove the packageResource goal (see "Supporting WTP" below for an example).

###src/test/javascript 
Store your Jasmine specs (i.e. `ninjaSpec.js`) in `src/test/javascript`. No need to create an HTML spec runner, one will be generated and executed for you by the **jasmine:test** goal!
    
Example test Output
-------------------
jasmine-maven-plugin behaves just like maven-surefire-plugin and will fail the build on spec failures (unless haltOnFailure is set to false). 

An example of some failing output follows:

		-------------------------------------------------------
		 J A S M I N E   S P E C S
		-------------------------------------------------------
		[INFO] 
		Slice-o-matic
		  occupies the SliceOMatic namespace
		  #slice
		    slices
		    does not dice
		  #dice
		    when the knob is turned to "Fine"
		      dices quite finely
		      does not cut off fingers
		    when the knob is turned to "Coarse"
		      dices rather roughly
		    when a hand is inserted into the Slice-o-matic
		      is a fantastic idea <<< FAILURE!
		        * Expected 'Are you kidding? That's a terrible idea!' to contain 'Great idea'.

		1 failure:

		  1.) Slice-o-matic #dice when a hand is inserted into the Slice-o-matic it is a fantastic idea <<< FAILURE!
		    * Expected 'Are you kidding? That's a terrible idea!' to contain 'Great idea'.
		[INFO] 
		Results:

		7 specs, 1 failures

		[INFO] ------------------------------------------------------------------------
		[ERROR] BUILD FAILURE
		[INFO] ------------------------------------------------------------------------
		[INFO] There were Jasmine spec failures.

Usage Notes
-----------
## Project layout
The jasmine-maven-plugin presumes a default project directory layout. If this layout doesn't suit your project, fear not, as it's entirely customizable. In addition to everything documented here,
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

## Goals

###jasmine:generateManualRunner
This goal binds to the generate-test-sources phase and will generate an extra spec runner HTML (named `ManualSpecRunner.html` by default) in the jasmine target directory (`target/jasmine`). 
This way, you can easily run your specs in the browser as you develop them, while still leaning on the plugin to keep the HTML up-to-date for you. Note that this HTML file is separate from 
the one generated during **jasmine:test**, because it points to the source directories directly.

When using the manual runner in a browser, be careful to edit your source & specs in the project's `src` directory, even though the runner itself runs in `target`! 

###jasmine:resources
This goal binds to the process-resources phase and copies the `src/main/javascript` directory into `target/jasmine/src`.  It can be changed by configuring a parameter named `jsSrcDir` in the plugin execution section of the POM.

###jasmine:testResources
This goal binds to the process-test-resources phase and copies the `src/test/javascript` directory into `target/jasmine/spec`.  It can be changed by configuring a parameter named `jsTestSrcDir` in the plugin execution section of the POM.

###jasmine:test
This goal binds to the test phase and generates a Jasmine runner file in `target/jasmine/SpecRunner.html` based on the sources processed by the previous two goals and Jasmine's own dependencies.  It will respect the `skipTests` property, and will not halt processing if `haltOnFailure` is set to false.

###jasmine:preparePackage 
This goal binds to the prepare-package phase and copies the production JavaScript sources from `target/jasmine/src` to `/js` within the package directory (e.g. `target/your-webapp/js`). The sub-path can be cleared or changed by setting the `packageJavaScriptPath` property

## Configuration

### jsSrcDir - Supporting WTP (Leaving your JavaScript sources in your `webapp` directory)
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

### sourceIncludes, sourceExcludes - Specifying patterns to include & exclude scripts
If the load order of your project's JavaScript matters, or if your base JavaScript directory contains script files or directories that shouldn't be included when your Jasmine specs are executed, you can specify string patterns to control which sources get included, excluded, and in which order by using the `sourceIncludes` configuration element.

The default includes pattern is "\*\*/\*.js" (that is, matching all files with the "js" extension in all sub-directories of `jsSrcDir`). It's likely that you'll want to include this pattern definition at the bottom of any custom patterns

**Example:** a project contains jQuery & a raft of jQuery plugins under the "vendor" directory; your scripts depend on jQuery, so it's imperative that jQuery loads first. Meanwhile, there's a "main.js" file on the root that must be included before our other application scripts. 

The configuration, therefore, might look like:

    <configuration>
      ...
      <sourceIncludes>
        <include>vendor/**/*.js</include>
        <include>main.js</include>
        <include>**/*.js</include>
      </sourceIncludes>
    </configuration>

Building on this example, if there were a script (say, "vendor/ajax.js") in your source directories that doesn't need to be included in the runner (perhaps it causes Jasmine execution to fail, or it consumes significant resources without being relevant to your suite of specs), it could be ignored with `sourceExcludes` like so:

    <configuration>
      ...
      <sourceExcludes>
        <exclude>vendor/ajax.js</exclude>
      </sourceExcludes>
    </configuration>

Per the `sourceIncludes` settings, everything under the vendor directory will still be loaded first, but "vendor/ajax.js" will be excluded from the runner HTML altogether.

### specIncludes, specExcludes - Specifying patterns to include & exclude specs
`specIncludes` and `specExcludes` are just like `sourceIncludes` and `sourceExcludes`, except they govern the files under your `jsTestSrcDir` base spec directory, as opposed to your production sources in your `jsSrcDir`.

These options will probably come in handy less frequently than their `sourceIncludes`/`sourceExcludes` analogues, because the load order of Jasmine spec scripts ought not matter. Still, perhaps an application-wide "spec-helper.js" (or a support library like [jasmine-jquery](https://github.com/velesin/jasmine-jquery) or [jasmine-fixture](https://github.com/searls/jasmine-fixture)) needs to be loaded before the specs themselves. That can be accomplished like so:

    <configuration>
      ...
      <specIncludes>
        <include>lib/jasmine-jquery.js</include>
        <include>lib/jasmine-fixture.js</include>
        <include>support/spec-helper.js</include>
        <include>**/*.js</include>
      </specIncludes>
    </configuration>

### preloadSources - Enforcing the precise order in which JavaScript files are loaded 

**Note: Prior to the release of version 1.0.2-beta-2, the `preloadSources` configuration was the only way to control the ordering of scripts. Now that the plugin supports includes/excludes with wildcards, the number of situations in which `preloadSources` is necessary should be dramatically reduced**

You can configure the plugin to load a list of JavaScript sources before any others. This is useful in situations where the above includes/excludes options fall short. One such situation is when a test-scoped file needs to overwrite the functionality of a production source, something one team ran into with a localization plugin. In that case, all of the included files will still be loaded, but anything found in `preloadSources` will be moved to the top, per the resolution rules at the bottom of this section.

    <configuration>
      <sourceIncludes>
        <include>vendor/**/*.js</include>
        <include>**/*.js</include>
      </sourceIncludes>

      <preloadSources>
        <!-- execute a script that overwrites another before the production sources load -->
        <source>vendor/jquery.js</source>
        <source>vendor/jquery.i18n.js</source>
        <source>hack-i18n-for-specs.js</source> <!-- resolves files in jsTestSrcDir using a relative path, too -->
          
        <!-- Even supports remote resources and arbitrary protocols -->
        <source>https://ajax.googleapis.com/ajax/libs/prototype/1.7.0.0/prototype.js</source>
      </preloadSources>				
    </configuration>
    
As demonstrated above, `preloadSources` will attempt to resolve each specified source in this order (before placing it in an HTML `script` tag):

1. As a file that exists relative to the `jsSrcDir`
2. As a file that  exists relative to the `jsTestSrcDir`
3. Exactly as entered into the POM (e.g. "http://../script.js", "ftp://blah.js", "/path/to/my-other-project/../script.js", etc.)

### customRunnerTemplate - Creating a custom SpecRunner HTML template

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
     
### debug - Debugging failures

The plugin relies on HtmlUnit to execute your specs "headlessly" in a console. If your specs pass when you open `target/jasmine/ManualSpecRunner.html` in each browser but fail when executing the `jasmine:test` goal, it's likely that something went wrong while HtmlUnit was executing. Unfortunately, these failures can be opaque and difficult to trace. If `jasmine:test` is timing out, you might consider trying to debug it with the `debug` configuration flag set to `true` and a shorter timeout to force faster failure.

    <configuration>
      ...
      <timeout>60</timeout> <!-- 60 second timeout -->
      <debug>true</timeout> <!-- attempt to print the spec runner results and build a JUnit report, even if spec execution times out. -->
    </configuration>

If you have experience debugging heap space or halting errors in HtmlUnit, please consider [opening an issue](https://github.com/searls/jasmine-maven-plugin/issues) to lend some advice on how jasmine-maven-plugin could be improved.

### browserVersion - Specifying which Browser to execute Jasmine specs with

By default, the plugin will execute the project's specs using HtmlUnit's "FIREFOX_3" [BrowserVersion](http://htmlunit.sourceforge.net/apidocs/com/gargoylesoftware/htmlunit/BrowserVersion.html).
If you'd like to execute your specs against a different one of its profiles, you can specify it in the plugin's configuration in your POM.
HtmlUnit currently only offers a few flavors of FireFox and IE (see [its JavaDoc for the exact names]((http://htmlunit.sourceforge.net/apidocs/com/gargoylesoftware/htmlunit/BrowserVersion.html)),
but here is an example configuration specifying that specs should be executed against HtmlUnit's IE6 profile:

    <configuration>
      ...
      <browserVersion>INTERNET_EXPLORER_6</browserVersion>				
    </configuration>

### format - Specifying the style of the console output 

You can configure how the plugin prints your specs to the console. By default, it will print them in a nested format that prints the full text of every `describe` and `it` name.  However, particularly for large projects, you can configure the plugin to output in a format similar to [rspec](https://github.com/rspec/rspec)'s progress format. Just specify configure the `format` parameter like so:

    <configuration>
      ...
      <format>progress</format>				
    </configuration>

And your output will be all the more terse: 

    -------------------------------------------------------
     J A S M I N E   S P E C S
    -------------------------------------------------------
    [INFO] 
    ................................................................................
    ................................................................................
    ................................................................................
    ..........................................


    Results: 282 specs, 0 failures    

### timeout - Specifying the timeout for spec execution

While executing your specs, the plugin will time out after 300 seconds (5 minutes) by default. In the event that your project simply has a *lot* of specs (or, perhaps, a really *slow* build machine), the timeout can be modified with the `timeout` configuration parameter.

    <configuration>
      ...
      <timeout>900</timeout>
    </configuration>

## JUnit XML Reports

The plugin's `test` goal will output the test results in a JUnit text XML report, located in `target/jasmine/TEST-jasmine.xml`. The implementation attempts to satisfy the most middle-of-the-road consensus as to what the schema-less XML report "[should](http://stackoverflow.com/questions/442556/spec-for-junit-xml-output)" look like.

As an example, to integrate the report into a Hudson job (note that it must be a **freestyle** job), select "Publish JUnit test result report" among the available "Post-build Actions" and include a file pattern like "\*\*/jasmine/TEST\*.xml". Once included, your jasmine specs will be counted and interactive in the same way your other tests are!

## Current Version Info

The plugin's version numbering will mirror the version of Jasmine that backs it. The latest version of the plugin points to Jasmine 1.0.2, so its version number is **1.0.2-beta-2**.

If you want to point at snapshot releases of the plugin, they're hosted on the [Sonatype OSS snapshot repository](https://oss.sonatype.org/service/local/repositories/snapshots).  

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
