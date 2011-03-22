Feature: displaying progress

  In order to work efficiently with projects on the order of hundreds of specs
  I want an alternative output format that's more concise
  
  Scenario: a project with progress format enabled
  
    Given I am currently in the "jasmine-webapp-progress-format" project
    When I run "mvn clean test"
    Then the build should succeed
    And I should see "[.\n]{285}"
    And I should see "Results: 282 specs, 0 failures"
  