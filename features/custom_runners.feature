Feature: using a custom runner template

  In order to work around an edge case that isn't anticipated by the plugin
  I want to create a custom runner HTML template that will be used to create both Jasmine Spec Runners
  
  Scenario: using a custom runner with a footer and jQuery in it and running headless test
  
    Given I am currently in the "jasmine-webapp-custom-runner" project
    When I run "mvn clean test"
    Then the build should succeed
    And the file "target/jasmine/SpecRunner.html" should contain "Copyright Acme, Inc."
  
  Scenario: using a custom runner from the classpath
  
    Given I am currently in the "jasmine-webapp-custom-runner-classpath" project
    When I run "mvn clean install"
    Then the build should succeed
    And the file "jasmine-webapp/target/jasmine/SpecRunner.html" should contain "Copyright Acme, Inc."
    
  Scenario: using a custom runner from a remote url
  
    Given I am currently in the "jasmine-webapp-custom-runner-remote" project
    When I run "mvn clean test"
    Then the build should succeed
    And the file "target/jasmine/SpecRunner.html" should contain "Copyright Acme, Inc."
    
  @server
  Scenario: using a custom runner with a footer and jQuery in it and running in browser test
  
    Given I am currently in the "jasmine-webapp-custom-runner" project
    When I run "mvn clean jasmine:bdd" in a new process
    And I load "http://localhost:8234" in a browser
    Then the file "target/jasmine/ManualSpecRunner.html" should contain "Copyright Acme, Inc."

  Scenario: specifying a custom runner but getting the path wrong
  
    Given I am currently in the "jasmine-webapp-custom-runner-missing" project
    When I run "mvn clean test"
    Then the build should fail
    And I should see "Invalid value for parameter 'customRunnerTemplate'. File does not exist: .*/src/test/resources/templates/specrunner.htmlfail"
