Feature: Additional contexts can be configured.

  In order to reference javascript files outside of the standard source and spec directories
  I want to be able to configure additional contexts

  Scenario: Reference a vendor script outside of the source directory

    Given I am currently in the "jasmine-webapp-additional-contexts" project
    When I run "../../../../mvnw clean install"
    Then the build should succeed
    And I should see "Results: 2 specs, 0 failures"

  @server
  Scenario: Reference a vendor script outside of the source directory in browser

    Given I am currently in the "jasmine-webapp-additional-contexts" project
    When I run "../../../../mvnw clean jasmine:bdd" in a new process
    And I load "http://localhost:8234" in a browser
    Then the page should contain "2 specs, 0 failures"
