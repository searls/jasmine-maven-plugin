Feature: integrate with other plugins

  Scenario: integrate with saga-maven-plugin

    Given I am currently in the "jasmine-webapp-saga-integration" project
    When I run "mvn clean verify"
    Then the build should succeed
    And I should see "Results: 1 specs, 0 failures"
    And I should see "Writing CSV coverage report"
    And the file "target/coverage/total-report.csv" should contain "src/HelloWorld.js,3,3,100%"

