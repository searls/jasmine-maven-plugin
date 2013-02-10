jasmine-maven-plugin
====================

[![Build Status](https://secure.travis-ci.org/searls/jasmine-maven-plugin.png)](http://travis-ci.org/searls/jasmine-maven-plugin)

For information on how to use jasmine-plugin, check out its [documentation page](http://searls.github.com/jasmine-maven-plugin/).

<strong><font color="red">7/31/2012</font></strong> - Heads up! [See below for an important note](https://github.com/searls/jasmine-maven-plugin#lifecycle-extensions) about a backwards-compatibility-breaking change in versions 1.2.0.0 & later.

## Current Version Info

The plugin's version numbering will mirror the version of Jasmine that backs it (with a version number tacked on at the end that tracks changes to the plugin within a Jasmine release). The latest version of the plugin points to Jasmine 1.2.0, so its version number is **1.2.0.0**.

If you want to point at snapshot releases of the plugin (note that I don't deploy them often), they're hosted on the [Sonatype OSS snapshot repository](https://oss.sonatype.org/service/local/repositories/snapshots).

## Maintainers
* [Justin Searls](http://about.me/searls), [Test Double](http://testdouble.com)
* [Kyle Lieber](http://kylelieber.com)

## Contributions
Pull requests are, of course, very welcome! A few todos, if anyone is interested in tackling them:

* JSLint and JSCoverage integration
* Parse & format ignored tests (currently only passing & failing tests are parsed)
* A facility that automatically executes the other goals if only `test` or `preparePackage` is configured to run.

## Acknowledgments
* Thanks to Pivotal Labs for authoring and publishing [Jasmine](http://github.com/pivotal/jasmine)
* Thanks to christian.nelson and sivoh1, owners of the [javascript-test-maven-plugin](http://code.google.com/p/javascript-test-maven-plugin/) project, which provided a similar implementation from which to glean several valuable lessons.

## Public Service Announcments

### Lifecycle extensions

[A change](https://github.com/searls/jasmine-maven-plugin/pull/92) was introduced in version 1.2.0.0 that will require a change to your POM file. Your POM configuration for jasmine-maven-plugin must now include a child element: `<extensions>true</extensions>` to work in version 1.2.0.0 and later. Sorry about the inconvenience, but this was the only way to [avoid each build phase from executing twice](https://github.com/searls/jasmine-maven-plugin/pull/54).

So, if you're arriving here from Google because you see an error like this when you run your build:

``` bash
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 0.389s
[INFO] Finished at: Tue Jul 31 17:05:33 EDT 2012
[INFO] Final Memory: 3M/81M
[INFO] ------------------------------------------------------------------------
[ERROR] Unknown lifecycle phase "jasmine-process-test-resources". You must specify a valid lifecycle phase or a goal in the format <plugin-prefix>:<goal> or <plugin-group-id>:<plugin-artifact-id>[:<plugin-version>]:<goal>. Available lifecycle phases are: validate, initialize, generate-sources, process-sources, generate-resources, process-resources, compile, process-classes, generate-test-sources, process-test-sources, generate-test-resources, process-test-resources, test-compile, process-test-classes, test, prepare-package, package, pre-integration-test, integration-test, post-integration-test, verify, install, deploy, pre-clean, clean, post-clean, pre-site, site, post-site, site-deploy. -> [Help 1]
[ERROR]
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR]
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/LifecyclePhaseNotFoundException
```

The solution is to add `<extensions>true</extensions>` to your POM, like this:

``` xml
  <build>
    <plugins>
      <plugin>
        <groupId>com.github.searls</groupId>
        <artifactId>jasmine-maven-plugin</artifactId>
        <version>1.2.0.0</version>

        <extensions>true</extensions>

        <executions>
          <execution>
            <goals>
              <goal>test</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>
```

If you have any problems, please [check the project issues](https://github.com/searls/jasmine-maven-plugin/issues).
