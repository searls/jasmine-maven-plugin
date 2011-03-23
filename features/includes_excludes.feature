Feature: setting custom includes & excludes

  In order to accomodate with complex hierarchies and dependency loading issues
  I want to set custom includes and excludes of my sources and specs
  
  Scenario: a project with a simple set of excludes
  
    Given I am currently in the "jasmine-webapp-basic-excludes" project
    When I run "mvn clean test"
    Then the build should succeed
    
  Scenario: a project limited to explicit includes
  
    Given I am currently in the "jasmine-webapp-basic-includes" project
    When I run "mvn clean test"
    Then the build should succeed
    And I should see "Results: 3 specs, 0 failures"
    