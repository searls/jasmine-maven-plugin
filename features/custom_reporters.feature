Feature: Support custom reporter

  In order to support advanced reporting
  I want the maven build to allow a custom reporter
  So that values can be stored in tests, and used later by the reporter

  Scenario: project with javascript using a custom reporter

    Given I am currently in the "jasmine-webapp-custom-reporters" project
    When I run "mvn clean test -X -e"
    Then the build should succeed
    And I should see "Results: 2 specs, 0 failures, 0 pending"
    And I should see "Hello World"
