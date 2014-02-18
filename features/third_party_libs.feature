Feature: JavaScript files from maven dependencies should be available at specified URLs.

  In order to use JavaScript files contained by dependent artifacts (*.jar and *.war)
  I want to setup those JS files at <preloadSources> section using /classpath/* and /webjar/* root paths.

  Scenario: just pass a build

    Given I am currently in the "jasmine-webapp-third-party-libs" project
    When I run "mvn clean install"
    Then the build should succeed
    And I should see "Results: 6 specs, 0 failures"
