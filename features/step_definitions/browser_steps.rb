
When /^I run "([^"\s]*)\s+([^"]*)" in a new process$/ do |command, args|
  if command == 'mvn'
    command = '../../../../mvnw'
  end
  args.prepend (command + ' ')
  @process = ChildProcess.send(:build,*args.split(' '))
  @process.start
  sleep 10
end


When /^I load "([^"]*)" in a browser$/ do |url|
  if @process && @process.alive?
    sleep 5
    visit url
  else
    raise "Server not running!"
  end
end

Then /page should contain "([^"]*)"$/ do |expected|
  expect(page).to have_content(expected)
end

