Given /^I am currently in the "([^"]*)" project$/ do |project_name|
  Dir.chdir PWD
  Dir.chdir("src/test/resources/examples/"+project_name)
end

When /^I run "([^"]*)"$/ do |command|
  @output = `#{command} 2>&1`
end

Then /^the build should fail$/ do
  @output.should match /BUILD FAILURE/
end

Then /^the build should succeed$/ do
  @output.should match /BUILD SUCCESS/
end

Then /^I should see "([^"]*)"$/ do |content|
  @output.should match /#{content}.*/
end

Then /^I should not see "([^"]*)"$/ do |content|
  @output.should_not match content
end

Then /^the file "([^"]*)" should contain "(.*)"$/ do |file_name,content|
  load_file(file_name).should match content
end

Then /^the file "([^"]*)" should have XML "(.*)"$/ do |file_name, xpath|
  Nokogiri::XML.parse(load_file(file_name)).xpath(xpath).length.should be >= 1
end

Given /^the file "([^"]*)" does exist$/ do |file_name|
  File.exist?(file_name).should be true
end

Then /^the file "([^"]*)" does not exist$/ do |file_name|
  File.exist?(file_name).should be false
end

private

def load_file file_name
  File.read(Dir.pwd+'/'+file_name)
end
