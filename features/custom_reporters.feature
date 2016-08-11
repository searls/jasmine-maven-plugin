Feature: Support custom reporter

  In order to support advanced reporting
  I want the maven build to allow a custom reporter
  So that values can be stored in tests, and used later by the reporter

  Scenario: project with javascript using a custom reporter

    Given I am currently in the "jasmine-webapp-custom-reporters" project
    When I run "mvn clean test"
    Then the build should succeed
    And I should see "Results: 2 specs, 0 failures, 0 pending"
    And I should see "Hello World"
    And the file "target/jasmine/TEST-jasmine.xml" should have XML "/testsuite[@errors=0 and @tests=2 and @failures=0 and @skipped=0]"
    And the file "target/jasmine/TEST-jasmine.log" should contain "Hello World"
