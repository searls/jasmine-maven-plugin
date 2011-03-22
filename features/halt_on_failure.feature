Feature: halt the build when a spec failure occurs

  In order to prevent unstable builds from finishing
  I want the maven build to terminate in failure when specs fail
  
  Scenario: project with a single failure

    Given I am currently in the "jasmine-webapp-single-failing" project
    When I run "mvn clean test"
    Then the build should fail
    And I should see "Results: 5 specs, 1 failure"
    And I should see "There were Jasmine spec failures"
  
  Scenario: project with multiple failures

    Given I am currently in the "jasmine-webapp-many-failing" project
    When I run "mvn clean test"
    Then the build should fail
    And I should see "Results: 5 specs, 4 failures"
    And I should see "There were Jasmine spec failures"

  Scenario: project with no failures
  
    Given I am currently in the "jasmine-webapp-passing" project
    When I run "mvn clean test"
    Then the build should succeed
    And I should see "Results: 1 specs, 0 failures"
    And I should not see "There were Jasmine spec failures"

  Scenario: project with failures and haltOnFailure set to false
  
    Given I am currently in the "jasmine-webapp-single-failing" project
    When I run "mvn clean test -DhaltOnFailure=false"
    Then I should see "Results: 5 specs, 1 failure"
    But the build should succeed