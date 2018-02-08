require 'nokogiri'
require 'capybara/cucumber'
require 'childprocess'
require 'selenium-webdriver'

PWD = Dir.pwd

Capybara.register_driver :headless_firefox do |app|  
  options = Selenium::WebDriver::Firefox::Options.new
  options.add_argument('--headless') 
  driver = Capybara::Selenium::Driver.new(
    app,
    browser: :firefox,
    options: options
  )
end 

Capybara.register_driver :headless_chrome do |app|  
  capabilities = Selenium::WebDriver::Remote::Capabilities.chrome(
    chromeOptions: { args: %w(headless disable-gpu) }
  )

  Capybara::Selenium::Driver.new app,
    browser: :chrome,
    desired_capabilities: capabilities
end 

Capybara.default_driver = :headless_chrome
Capybara.default_max_wait_time = 5
