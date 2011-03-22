Given /^I am currently in the "([^"]*)" project$/ do |project_name|
  Dir.chdir PWD
  Dir.chdir("src/test/resources/examples/"+project_name)
end

When /^I run "([^"]*)"$/ do |command|
  @output = `#{command}`
end

Then /^the build should fail$/ do
  @output.should match /BUILD FAILURE/
end

Then /^the build should succeed$/ do
  @output.should match /BUILD SUCCESS/
end

Then /^I should see "([^"]*)"$/ do |content|
  @output.should match content
end

Then /^I should not see "([^"]*)"$/ do |content|
  @output.should_not match content
end

Then /^the file "([^"]*)" should contain "(.*)"$/ do |file_name,content|
  File.read(Dir.pwd+'/'+file_name).should match content
end
