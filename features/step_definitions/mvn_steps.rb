Given /^I am currently in the "([^"]*)" project$/ do |project_name|
  Dir.chdir PWD
  Dir.chdir("target/test-classes/examples/"+project_name)
end

When /^I run "([^"]*)"$/ do |command|
  @output = `#{command} 2>&1`
end

Then /^the build should fail$/ do
  expect(@output).to match /BUILD FAILURE/
end

Then /^the build should succeed$/ do
  expect(@output).to match /BUILD SUCCESS/
end

Then /^I should see "([^"]*)"$/ do |content|
  expect(@output).to match /#{content}.*/
end

Then /^I should not see "([^"]*)"$/ do |content|
  expect(@output).not_to match content
end

Then /^the file "([^"]*)" should contain "(.*)"$/ do |file_name,content|
  expect(load_file(file_name)).to match content
end

Then /^the file "([^"]*)" should not contain "(.*)"$/ do |file_name,content|
  expect(load_file(file_name)).not_to match content
end

Then /^the file "([^"]*)" should have XML "(.*)"$/ do |file_name, xpath|
  expect(Nokogiri::XML.parse(load_file(file_name)).xpath(xpath).length).to be >= 1
end

Given /^the file "([^"]*)" does exist$/ do |file_name|
  expect(File.exist?(file_name)).to be true
end

Then /^the file "([^"]*)" does not exist$/ do |file_name|
  expect(File.exist?(file_name)).to be false
end

private

def load_file file_name
  File.read(Dir.pwd+'/'+file_name)
end
