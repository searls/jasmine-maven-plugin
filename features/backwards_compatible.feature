Feature: Introduce new features while preserving backwards compatibility

  Scenario: Build should pass using legacy version of the default SpecRunner html template.

    Given I am currently in the "backwards-compatible/jasmine-webapp-passing" project
    When I run "mvn clean test"
    Then the build should succeed
    And I should see "Results: 1 specs, 0 failures"

  Scenario: Build should pass using legacy version of the requirejs SpecRunner html template.

    Given I am currently in the "backwards-compatible/jasmine-webapp-amd-support" project
    When I run "mvn clean test"
    Then the build should succeed
    And I should see "Results: 2 specs, 0 failures"
  
  Scenario: Build should pass using legacy version of the requirejs SpecRunner html template with custom config.

    Given I am currently in the "backwards-compatible/jasmine-webapp-advanced-requirejs" project
    When I run "mvn clean test"
    Then the build should succeed
    
  @server
  Scenario: Runs specs in browser using legacy version of the default SpecRunner html template.

    Given I am currently in the "backwards-compatible/jasmine-webapp-passing" project
    When I run "mvn clean jasmine:bdd" in a new process
    And I load "http://localhost:8234" in a browser
    Then the page should contain "Passing 1 spec"

  @server
  Scenario: Runs specs in browser using legacy version of the requirejs SpecRunner html template.

    Given I am currently in the "backwards-compatible/jasmine-webapp-amd-support" project
    When I run "mvn clean jasmine:bdd" in a new process
    And I load "http://localhost:8234" in a browser
    Then the page should contain "Passing 2 spec"
  
  @server
  Scenario: Runs specs in browser using legacy version of the requirejs SpecRunner html template with custom config.

    Given I am currently in the "backwards-compatible/jasmine-webapp-advanced-requirejs" project
    When I run "mvn clean jasmine:bdd" in a new process
    And I load "http://localhost:8234" in a browser
    Then the page should contain "Passing 1 spec"
