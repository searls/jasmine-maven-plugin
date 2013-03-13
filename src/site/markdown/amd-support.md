AMD Support
===========

If you are using an AMD implementation like require.js then the default [Spec Runner Template](spec-runner-templates.html) will not work for you. We have provided an alternate template that should work with require.js. To use that template just set the value of the `specRunnerTemplate` parameter to `REQUIRE_JS`.

Here is an example configuration:

```
<plugin>
    <groupId>com.github.searls</groupId>
    <artifactId>jasmine-maven-plugin</artifactId>
    <version>${jasmine-maven-plugin.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>test</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <specRunnerTemplate>REQUIRE_JS</specRunnerTemplate>
        <preloadSources>
            <source>lib/require.js</source>
        </preloadSources>
    </configuration>
</plugin>
```

This template expects that you will wrap all of your specs in a `define` call so that they can be loaded as modules and in turn load any dependencies needed. So for example, if you had the follow module to be tested:

```
// example.js
define(function() {
  return {
    hello : function(name) {
      return "hello " + name;
    };
  };
});
```

Then the spec would look like this:

```
// example.spec.js
define("example", function(example) {
  describe("example.hello", function() {
    it("should return 'hello jim'", function() {
      expect(example.hello("jim")).toBe("hello jim");
    });
  });
});
```

More Examples
-------------
We use a couple of example projects for testing the AMD support. You may find it helpful to look at them as well:
 * [jasmine-webapp-amd-support](https://github.com/searls/jasmine-maven-plugin/tree/master/src/test/resources/examples/jasmine-webapp-amd-support)
 * [jasmine-webapp-advanced-requirejs](https://github.com/searls/jasmine-maven-plugin/tree/master/src/test/resources/examples/jasmine-webapp-advanced-requirejs)

Alternatives
============
Not everyone is going to like wrapping all of their specs in `define` calls.  The fact is there are many different ways to use Jasmine with Require.js. So the built-in template is really only there to provide you a simple way to get started. If you are looking for a different approach, I suggest reading up on writing a custom runner template in our [Spec Runner Templates](spec-runner-templates.html) guide.

For instance, say you would prefer to use [testr.js](https://github.com/mattfysh/testr.js) to load your modules into your specs. Here is an example custom runner template that would do just that:

``` html
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=$sourceEncoding$">
  <title>Jasmine Spec Runner</title>
  $cssDependencies$
  $javascriptDependencies$
  $preloadScriptTags$
</head>
<body>
  <script type="text/javascript">
    if(window.location.href.indexOf("ManualSpecRunner.html") !== -1) {
      document.body.appendChild(document.createTextNode("Warning: opening this HTML file directly from the file system is deprecated. You should instead try running `mvn jasmine:bdd` from the command line, and then visit `http://localhost:8234` in your browser. "))
    }
    var sources = $sourcesList$;
    var sourceDir = '$sourceDir$';

    // remove sourceDir and .js extension from each source
    for (var i=0; i < sources.length; i++) {
      sources[i] = sources[i].replace(sourceDir+'/','').replace(/\.js\$/,'');
    }   

    $if(customRunnerConfiguration)$
    $customRunnerConfiguration$
    $else$
      require.config({
        baseUrl: sourceDir
      });

      testr.config({
        specBaseUrl: '$specDir$',
        autoLoad: true
      });
    $endif$
    
    require(sources, function() {
      var currentWindowOnload = window.onload;

      window.onload = function() {
        if (currentWindowOnload) {
          currentWindowOnload();
        }
        execJasmine();
      };
      
      function execJasmine() {
        window.reporter = new jasmine.$reporter$(); jasmine.getEnv().addReporter(reporter);
        jasmine.getEnv().execute();
      }
    });
  </script>
</body>
</html>
```

The configuration for the template would just look something like this:

```
<plugin>
  <groupId>com.github.searls</groupId>
  <artifactId>jasmine-maven-plugin</artifactId>
  <version>${jasmine-maven-plugin.version}</version>
  <executions>
    <execution>
      <goals>
        <goal>test</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <preloadSources>
        <source>lib/require.js</source>
        <source>lib/testr.js</source>
      </preloadSources>
    <customRunnerTemplate>src/test/config/testr_runner.htmltemplate</customRunnerTemplate>
  </configuration>
</plugin>
```
