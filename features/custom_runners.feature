Feature: using a custom runner template

  In order to work around an edge case that isn't anticipated by the plugin
  I want to create a custom runner HTML template that will be used to create both Jasmine Spec Runners
  
  Scenario: using a custom runner with a footer and jQuery in it
  
    Given I am currently in the "jasmine-webapp-custom-runner" project
    When I run "mvn clean test"
    Then the build should succeed
    And the file "target/jasmine/SpecRunner.html" should contain "Copyright Acme, Inc."
    And the file "target/jasmine/ManualSpecRunner.html" should contain "Copyright Acme, Inc."

  Scenario: specifying a custom runner but getting the path wrong
  
    Given I am currently in the "jasmine-webapp-custom-runner-missing" project
    When I run "mvn clean test"
    Then the build should fail
    And I should see "Caused by: java.io.FileNotFoundException: File '.*/src/test/resources/templates/specrunner.htmlfail' does not exist"