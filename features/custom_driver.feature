Feature: setting a custom WebDriver
  In order to test browser-specific behavior
  I want to specify a custom WebDriver and options

  Scenario: Using HtmlUnitDriver

    Given I am currently in the "jasmine-webapp-htmlunit" project
    When I run "mvn clean test"
    Then the build should succeed
    And I should see "has Firefox in its user agent string"

  @server
  Scenario: Using ChromeDriver with custom ChromeOptions

    Given I am currently in the "jasmine-webapp-chromeoptions" project
    When I run "mvn clean test"
    Then the build should succeed
    And I should see "Results: 1 specs, 0 failures"


