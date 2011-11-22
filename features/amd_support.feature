Feature: Support amd modules in javaScript

  In order to support amd modules
  I want the maven build to include specs using require.js format instead of script tags
  So that tests can be loaded using the require.js scriptloader

  Scenario: project with javascript using require.js

    Given I am currently in the "jasmine-webapp-amd-support" project
    When I run "mvn clean test"
    Then the build should succeed
    And I should see "Results: 2 specs, 0 failures"