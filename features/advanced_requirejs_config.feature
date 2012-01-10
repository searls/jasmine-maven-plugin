Feature: Support custom require.js configuration

  In order to support advanced scriptloading using require.js
  I want the maven build to include custom configuration elements
  So that all scripts can be loaded using the require.js scriptloader

  Scenario: project with javascript using require.js and custom configuration

    Given I am currently in the "jasmine-webapp-advanced-requirejs" project
    When I run "mvn clean test"
    Then the build should succeed
    And I should see "Results: 1 specs, 0 failures"

  @server
  Scenario: project with javascript using require.js and custom configuration running in browser

    Given I am currently in the "jasmine-webapp-advanced-requirejs" project
    When I run "mvn clean jasmine:bdd" in a new process
    And I load "http://localhost:8234" in a browser
    Then I should see "1 spec, 0 failures"
