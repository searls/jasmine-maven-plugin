Feature: setting a custom character encoding

  In order to supply a particular encoding for my specs
  I want control over the charset of the generated runner files

  Scenario: setting a custom character encoding

    Given I am currently in the "jasmine-webapp-custom-encoding" project
    When I run "mvn clean test"
    Then the build should succeed
    And the file "target/jasmine/SpecRunner.html" should contain "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">"
    And the file "target/jasmine/ManualSpecRunner.html" should contain "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">"

  Scenario: leaving the default character encoding

    Given I am currently in the "jasmine-webapp-passing" project
    When I run "mvn clean test"
    Then the build should succeed
    And the file "target/jasmine/SpecRunner.html" should contain "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
    And the file "target/jasmine/ManualSpecRunner.html" should contain "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"