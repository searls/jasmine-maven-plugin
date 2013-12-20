Feature: skipping tests

  In order to build quickly or when the build is known to be unstable
  I want to skip tests from the command line 
  
  Scenario: skipping tests with -DskipTests

    Given I am currently in the "jasmine-webapp-single-failing" project
    When I run "mvn clean test -DskipTests"
    Then the build should succeed
    And I should not see "Results: 5 specs, 1 failure"
    And I should not see "There were Jasmine spec failures"

  Scenario: setting -DskipTests=false

    Given I am currently in the "jasmine-webapp-single-failing" project
    When I run "mvn clean test -DskipTests=false"
    Then the build should fail
    And I should see "Results: 5 specs, 1 failure"
    And I should see "There were Jasmine spec failures"

  Scenario: customRunnerConfiguration is missing but we ran with -DskipTests

      Given I am currently in the "jasmine-webapp-custom-runner-missing" project
      When I run "mvn clean test -DskipTests"
      Then the build should succeed
    