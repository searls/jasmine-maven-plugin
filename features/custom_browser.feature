Feature: setting a custom browser

  In order to test browser-specific behavior
  I want to specify the HtmlUnit browser profile for my project
  
  Scenario: setting a browser to IE6
  
    Given I am currently in the "jasmine-webapp-custom-browser" project
    When I run "mvn clean test"
    Then the build should succeed
    And I should see "has MSIE in its user agent string"

    
