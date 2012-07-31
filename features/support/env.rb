require 'nokogiri'
require 'capybara/cucumber'
require 'childprocess'

PWD = Dir.pwd

Capybara.default_driver = :selenium
Capybara.default_wait_time = 5