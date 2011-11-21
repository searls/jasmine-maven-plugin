After('@server') do
  @browser.close
  @process.stop
end