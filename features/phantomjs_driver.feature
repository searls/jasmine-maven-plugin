Feature: using the phantomJS webdriver

  In order to test modern browser features
  I want to be able to use PhantomJSDriver instead of HtmlUnitDriver
  
  Scenario: using PhantomJsDriver instead of HtmlUnitDriver
  
    Given I am currently in the "jasmine-webapp-phantomjs" project
    When I run "mvn clean test"
    Then the build should succeed
    And I should see "Results: 1 specs, 0 failures"
