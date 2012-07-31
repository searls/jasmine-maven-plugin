require 'nokogiri'
require 'capybara/cucumber'
require 'childprocess'

PWD = Dir.pwd

Capybara.default_driver = :selenium
