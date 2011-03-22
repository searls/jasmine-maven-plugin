Feature: specifying the loading of sources in the POM

  In order to control the execution order of scripts
  I want to specify the order of source files, spec files, and completely remote files in the POM
  
  Scenario: project with production sources sensitive to ordering 

    Given I am currently in the "jasmine-webapp-order-matters" project
    When I run "mvn clean test"
    Then the build should succeed

  Scenario: project with spec sources sensitive to ordering
  
    Given I am currently in the "jasmine-webapp-spec-order-matters" project
    When I run "mvn clean test"
    Then the build should succeed
  
  Scenario: project depending that some remote script be preloaded
  
    Given I am currently in the "jasmine-webapp-load-remote" project
    When I run "mvn clean test"
    Then the build should succeed
  
