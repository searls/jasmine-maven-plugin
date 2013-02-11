file = new File(basedir, 'build.log')
assert file

text = file.text
assert text.contains("specrunner.htmlfail' does not exist")

return true