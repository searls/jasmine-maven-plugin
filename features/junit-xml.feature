Feature: create JUnit XML reports

  In order to leverage reporting tools that consume JUnit-style XML reports
  I want the plugin to generate an XML file for each test execution
  
  Scenario: project with a single passing spec
  
    Given I am currently in the "jasmine-webapp-passing" project
    When I run "mvn clean test"
    Then the build should succeed
    And the file "target/jasmine/TEST-jasmine.xml" should have XML "/testsuite[@errors=0 and @tests=1 and @failures=0 and @skipped=0]"
    And the file "target/jasmine/TEST-jasmine.xml" should have XML "/testsuite/testcase[@name="HelloWorld should say hello" and @failure="false"]"
    
  Scenario: project with a single failing spec

    Given I am currently in the "jasmine-webapp-single-failing" project
    When I run "mvn clean test"
    Then the build should fail
    And the file "target/jasmine/TEST-jasmine.xml" should have XML "/testsuite[@errors=0 and @tests=5 and @failures=1 and @skipped=0]"
    And the file "target/jasmine/TEST-jasmine.xml" should have XML "/testsuite/testcase[@name="HelloWorld loses" and @failure="true"]"
    And the file "target/jasmine/TEST-jasmine.xml" should have XML "/testsuite/testcase[@failure="true"]/error[@type="expect.toBe" and @message="Expected 'sad' to be 'panda'." and text()="Expected 'sad' to be 'panda'."]"