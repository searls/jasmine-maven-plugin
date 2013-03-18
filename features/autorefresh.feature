Feature: auto refresh the test runner

  In order to have constant test feedback as I modify my code.
  I want to be able to set the test runner to refresh at an interval I choose.
  
  @server
  Scenario: default settings should not auto refresh

    Given I am currently in the "jasmine-webapp-passing" project
    When I run "mvn clean jasmine:bdd" in a new process
    And I load "http://localhost:8234" in a browser
    Then the file "target/jasmine/ManualSpecRunner.html" should not contain "<meta http-equiv=\"refresh\""
  
  @server
  Scenario: auto refresh every 5 seconds

    Given I am currently in the "jasmine-webapp-passing" project
    When I run "mvn clean jasmine:bdd -Djasmine.autoRefreshInterval=5" in a new process
    And I load "http://localhost:8234" in a browser
    Then the file "target/jasmine/ManualSpecRunner.html" should contain "<meta http-equiv=\"refresh\" content=\"5\">"

  Scenario: auto refresh should not apply to jasmine:test goal

    Given I am currently in the "jasmine-webapp-passing" project
    When I run "mvn clean test -Djasmine.autoRefreshInterval=5"
    Then the build should succeed
    And the file "target/jasmine/SpecRunner.html" should not contain "<meta http-equiv=\"refresh\""