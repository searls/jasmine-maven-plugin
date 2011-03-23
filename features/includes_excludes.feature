Feature: setting custom includes & excludes

  In order to accomodate with complex hierarchies and dependency loading issues
  I want to set custom includes and excludes of my sources and specs
  
  Scenario: a project with a simple source include/excludes
  
    Given I am currently in the "jasmine-webapp-basic-excludes" project
    When I run "mvn clean test"
    Then the build should succeed
    