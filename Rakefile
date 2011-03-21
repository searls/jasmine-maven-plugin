task :default => [:install]

task :install do
  system "mvn clean install"  
end

task :cucumber => [:install] do
  system "bundle exec cucumber"
end

task :deploy => [:install, :cucumber] do
  system "mvn deploy"
end