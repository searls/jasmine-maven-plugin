task :default => [:install]

task :install do
  sh "mvn clean install" do |ok, res|
    ok or fail "Failed to install with status #{res.exitstatus}"
  end
end

task :cucumber => [:install] do
  sh "bundle exec cucumber" do |ok, res|
    ok or fail "Failed to execute all feature tests with status #{res.exitstatus}"
  end
end

task :relish => [:cucumber] do
  sh "bundle exec relish push searls/jasmine-maven-plugin" do |ok, res|
    ok or fail "Failed to push to relish with status #{res.exitstatus}"
  end
end

task :deploy => [:install, :cucumber] do
  sh "mvn deploy" do |ok, res|
    ok or fail "Failed to deploy with status #{res.exitstatus}"
  end
end
