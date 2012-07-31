
When /^I run "([^"]*)" in a new process$/ do |command|
  @process = ChildProcess.build command
  @process.start
  sleep 3
end


When /^I load "([^"]*)" in a browser$/ do |url|
  if @process && @process.alive?
    sleep 2
    visit url
  else
    raise "Server not running!"
  end
end

Then /page should contain "([^"]*)"$/ do |expected|
  page.should have_content(expected)
end