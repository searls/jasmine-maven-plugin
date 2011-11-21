Feature: running a local server

  In order to do all sorts of wonderful things:
    like test ajax in browsers that don't like file:// ajax or load coffeescript
    I want to be able to hit a server and see mah specs

  @server
  Scenario: running the specs in a browser

    Given I am currently in the "jasmine-webapp-passing" project
    When I run "mvn clean jasmine:bdd" in a new process
    And I load "http://localhost:8234" in a browser
    Then I should see "1 spec, 0 failures"


