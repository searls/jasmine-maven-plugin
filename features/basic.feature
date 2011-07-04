Feature: just run some simple specs as part of the build

  Scenario: just pass a build

    Given I am currently in the "jasmine-webapp-passing" project
    When I run "mvn clean test"
    Then the build should succeed
    And I should see "Results: 1 specs, 0 failures"

  Scenario: run a build explicitly using the jasmine:test goal

    Given I am currently in the "jasmine-webapp-passing" project
    When I run "mvn clean jasmine:test"
    Then the build should succeed
    And I should see "Results: 1 specs, 0 failures"