Feature: specifying the loading of sources in the POM

  In order to control the execution order of scripts
  I want to specify the order of source files and spec files using regular expressions in the POM
  
  Scenario: project with production sources sensitive to ordering 

    Given I am currently in the "jasmine-webapp-preload-patterns" project
    When I run "mvn clean test"
    Then the build should succeed
