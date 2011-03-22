Feature: Bug: Projects with a 10-based number of failures show as successful

  Because "30 failures" ends in "0 failures", the build succeeds
  Make it not do that.
  
  Scenario: a project with 10 failures
  
    Given I am currently in the "jasmine-webapp-10-failing" project
    When I run "mvn clean test"
    Then the build should fail
  