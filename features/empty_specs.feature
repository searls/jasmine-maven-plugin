Feature: the build should finish gracefully even if the user's POM configuration is incomplete

  Scenario: project with no failures

    Given I am currently in the "jasmine-webapp-empty-example" project
    When I run "mvn clean test"
    Then the build should succeed
    And I should see "Results: 0 specs, 0 failures"