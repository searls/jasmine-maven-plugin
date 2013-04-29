jasmine-maven-plugin
====================

[![Build Status](https://secure.travis-ci.org/searls/jasmine-maven-plugin.png)](http://travis-ci.org/searls/jasmine-maven-plugin)

For information on how to use jasmine-plugin, check out its [documentation page](http://searls.github.com/jasmine-maven-plugin/).

<strong>4/28/2013</strong> - 1.3.1.2 has just been released. Only bug fixes in this release. See issues [#175](https://github.com/searls/jasmine-maven-plugin/issues/175) and [#176](https://github.com/searls/jasmine-maven-plugin/issues/176) for details.

<strong>4/13/2013</strong> - 1.3.1.1 has just been released and should be available in Maven Central soon. You can find full list of fixes/enhancements [here](http://searls.github.com/jasmine-maven-plugin/github-report.html). This is a quick summary:
 * Added support for using [PhantomJS](http://phantomjs.org/) through [GhostDriver](https://github.com/detro/ghostdriver). For configuration examples see [here](http://searls.github.io/jasmine-maven-plugin/phantomjs.html).
 * Allow passing filesystem path, a URL, or a classpath resource for `customRunnerTemplate` and `customRunnerConfiguration` parameters.
 * Fixed a few bugs with path generation in the spec runner. See [#169](https://github.com/searls/jasmine-maven-plugin/issues/169) and [#170](https://github.com/searls/jasmine-maven-plugin/issues/170) for details.
 * Added [autoRefreshInterval](http://searls.github.io/jasmine-maven-plugin/bdd-mojo.html#autoRefreshInterval) parameter to the `jasmine:bdd` goal. When this parameter is set the spec runner will automatically refresh at the set interval.
 
<strong>3/12/2013</strong> - 1.3.1.0 has been released and is now available in Maven Central. You can find the full list of fixes/enhancements [here](http://searls.github.com/jasmine-maven-plugin/github-report.html). This is a quick summary:
 * Published a full maven site for the plugin that includes documentation of all available parameters.  Check it out [here](http://searls.github.com/jasmine-maven-plugin).
 * Removed the deprecated `jasmine:preparePackage` goal. Please migrate to [wro4j](http://code.google.com/p/wro4j/) for such functionality.
 * Plugin no longer uses a custom lifecycle so you no longer need `<extensions>true</extensions>` in your configuration.
 * The `jasmine:test` goal now runs tests through Jetty the same as `jasmine:bdd` (see [here](http://searls.github.com/jasmine-maven-plugin/code-coverage.html) for proper config if you are using [saga-maven-plugin](http://timurstrekalov.github.com/saga/)) improving consistency between the goals.
 * Made more variables available to custom spec runner templates and documented those variables [here](http://searls.github.com/jasmine-maven-plugin/spec-runner-templates.html).
 * Improved AMD support which has been documented [here](http://searls.github.com/jasmine-maven-plugin/amd-support.html). Note the `scriptLoaderPath` parameter has been [deprecated](http://searls.github.com/jasmine-maven-plugin/test-mojo.html#scriptLoaderPath) and will be removed in the future.

<strong>7/31/2012</strong> - Heads up! [See below for an important note](https://github.com/searls/jasmine-maven-plugin#lifecycle-extensions) about a backwards-compatibility-breaking change in versions 1.2.0.0 & later.

## Current Version Info

The plugin's version numbering will mirror the version of Jasmine that backs it (with a version number tacked on at the end that tracks changes to the plugin within a Jasmine release). The latest version of the plugin points to Jasmine 1.3.1, so its version number is **1.3.1.0**.

If you want to point at snapshot releases of the plugin (note that I don't deploy them often), they're hosted on the [Sonatype OSS snapshot repository](https://oss.sonatype.org/service/local/repositories/snapshots).

## Maintainers
* [Justin Searls](http://about.me/searls), [Test Double](http://testdouble.com)
* [Kyle Lieber](http://kylelieber.com)

## Issues

If you have any problems, please [check the project issues](https://github.com/searls/jasmine-maven-plugin/issues).

## Contributions

Pull requests are, of course, very welcome! Please read our [contributing to the project](https://github.com/searls/jasmine-maven-plugin/wiki/Contributing-to-the-project) guide first. Then head over to the [open issues](https://github.com/searls/jasmine-maven-plugin/issues) to see what we need help with. Make sure you let us know if you intend to work on something. Also, check out the [milestones](https://github.com/searls/jasmine-maven-plugin/issues/milestones) to see what is planned for future releases.

## Acknowledgments
* Thanks to Pivotal Labs for authoring and publishing [Jasmine](http://github.com/pivotal/jasmine)
* Thanks to christian.nelson and sivoh1, owners of the [javascript-test-maven-plugin](http://code.google.com/p/javascript-test-maven-plugin/) project, which provided a similar implementation from which to glean several valuable lessons.
* Thanks to all who have contributed with pull requests, issues, suggestions.

