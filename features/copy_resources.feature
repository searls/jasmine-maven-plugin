Feature: copying resources

  In order to prevent version control systems from going haywire
  I want to exclude hidden files and directories from resource copying
    
  Scenario: a project with hidden files & directories
  
    Given I am currently in the "jasmine-webapp-copy-non-js" project
    And the file "src/main/javascript/.svn/foo" does exist
    And the file "src/main/javascript/.bar" does exist
    When I run "mvn clean test"
    Then the build should succeed
    And the file "target/jasmine/src/.svn/foo" does not exist
    And the file "target/jasmine/src/.bar" does not exist
    
