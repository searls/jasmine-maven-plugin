jasmine-maven-plugin
====================

For information on how to use jasmine-plugin, check out its [documentation](http://searls.github.com/jasmine-maven-plugin/).

## Current Version Info

The plugin's version numbering will mirror the version of Jasmine that backs it. The latest version of the plugin points to Jasmine 1.1.0, so its version number is **1.1.0**.

If you want to point at snapshot releases of the plugin (note that I don't deploy them often), they're hosted on the [Sonatype OSS snapshot repository](https://oss.sonatype.org/service/local/repositories/snapshots).

## Maintainers
* [Justin Searls](http://about.me/searls), [Test Double](http://testdouble.com)

## Contributions
Pull requests are, of course, very welcome! A few todos, if anyone is interested in tackling them:

* JSLint and JSCoverage integration
* Parse & format ignored tests (currently only passing & failing tests are parsed)
* A facility that automatically executes the other goals if only `test` or `preparePackage` is configured to run.

## Acknowledgments
* Thanks to Pivotal Labs for authoring and publishing [Jasmine](http://github.com/pivotal/jasmine)
* Thanks to christian.nelson and sivoh1, owners of the [javascript-test-maven-plugin](http://code.google.com/p/javascript-test-maven-plugin/) project, which provided a similar implementation from which to glean several valuable lessons.
