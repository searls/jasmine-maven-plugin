Spec Runner Templates
=====================
The jasmine-maven-plugin uses a simple templating system for generating a Jasmine [Spec Runner](http://pivotal.github.com/jasmine/#section-The_Runner_and_Reporter). The plugin comes with some built-in templates. You can use one of these built-in templates using the `specRunnerTemplate` parameter. Here is a list of the current built-in templates:

 * **[DEFAULT](https://github.com/searls/jasmine-maven-plugin/blob/master/src/main/resources/jasmine-templates/SpecRunner.htmltemplate)** : this is the default runner, it simply loads the preloads, then the sources, and finally the specs. Then it adds an onload event to execute jasmine.
 
 * **[REQUIRE_JS](https://github.com/searls/jasmine-maven-plugin/blob/master/src/main/resources/jasmine-templates/RequireJsSpecRunner.htmltemplate)** : this runner provides support for require.js. It expects you to wrap each of your specs in a `define` so that require.js dependencies can be pulled in.

Custom Runner Templates
=======================
If the built-in templates do not suit your needs you can write a custom template. You simply omit the `specRunnerTemplate` parameter and instead use the `customRunnerTemplate` parameter to specify the location of your template file. The template system is based on [StringTemplate](http://www.stringtemplate.org/) template engine so you may want to familiarize yourself with it. I would recommend starting with one of the built-in templates above and then modifying it to suit your needs rather than writing the template from scratch.

Template Variables
------------------
The following variables are available for use in your custom template:

 * **cssDependencies** : includes all the styles needed for the Jasmine HtmlReporter.
 * **javascriptDependencies** : includes all the javascript for jasmine.
 * **reporter** : the name of the jasmine reporter being used. This will be `HtmlReporter` during the `jasmine:bdd` goal and `JsApiReporter` during the `jasmine:test` goal.
 * **sourceEncoding** : the encoding to be used in the runner.
 * **preloadScriptTags** : this renders script tags for each of the scripts specified using the `preloadSources` parameter.
 * **sourceScriptTags** : this renders script tags for each of the sources found in the directory specified using the `jsSrcDir` parameter.
 * **specScriptTags** : this renders script tags for each of the specs found in the directory specified using the `jsTestSrcDir` parameter.
 * **allScriptTags** : this is a convenience variable the renders script tags for the preloads, then the sources, and finally the specs.
 * **preloadsList** : this renders a javascript array containing each of the scripts specified using the `preloadSources` parameter.
 * **sourcesList** : this renders a javascript array containing each of the sources found in the directory specified using the `jsSrcDir` parameter.
 * **specsList** : this renders a javascript array containing each of the specs found in the directory specified using the `jsTestSrcDir` parameter.
 * **allScriptsList** : this renders a javascript array containg all the scripts in the following order: preloads, sources, specs.
 * **sourceDir** : the path to the sources when deployed to the jetty server. Should be the same as the `srcDirectoryName` parameter.
 * **specDir** : the path to the specs when deployed to the jetty server. Should be the same as the `specDirectoryName` parameter.
 * **customRunnerConfiguration** : this renders the contents of the file specified by the `customRunnerConfiguration` parameter. Use this is you want to provide a way for adding some external customization to you template. It is most likely not that useful though if you are already writing a custom template.
 * **autoRefresh** : this is a boolean indicating whether or not the page should be refreshed automatically at an interval. It will be true with the `autoRefreshInterval` plugin parameter is greater than 0 and the `jasmine:bdd` goal is being used.
 * **autoRefreshInterval** : the interval at which the page should be automatically refreshed. Should be the same as the `autoRefreshInterval` parameter.

These variables were used in older versions of the plugin and have been kept for backwards compatibility. They should be considered deprecated and will be removed in a later version of the plugin.

 * **sources** : this is a synonym for the **allScriptTags** variable. If you have a template using this variable you should replace it with **allScriptTags**.
 * **priority** : this is a synonym for the **preloadsList** variable. If you have a template using this variable you should replace it with **preloadsList**.
 * **requirejsPath** : this will be the full path to the script loader specified using the `scriptLoaderPath` parameter (which is also deprecated). Instead of using this variable you should just include your script loader as a preload source and then use the **preloadScriptTags** variable.
