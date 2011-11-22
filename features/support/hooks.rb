After('@server') do
  @browser.close
  @process.stop
  @process.poll_for_exit(5)
  puts "Process killed"
end