Feature: support coffeescript

Scenario: A project with some coffee in it
	Given I am currently in the "jasmine-webapp-coffee" project
	When I run "mvn clean test"
	Then the build should succeed
	And I should see "Results: 2 specs, 0 failures"