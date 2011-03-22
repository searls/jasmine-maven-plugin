Feature: custom source and spec directories

	In order to organize the project how I see fit
	I want to specify custom directories for my JavaScript source and specs
	
  Scenario: custom spec and source directories
  
    Given I am currently in the "jasmine-webapp-custom-dirs" project
    When I run "mvn clean test"
    Then the build should succeed
    And I should see "Results: 1 specs, 0 failures"
  