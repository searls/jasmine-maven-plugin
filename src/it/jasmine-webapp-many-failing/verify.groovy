file = new File(basedir, 'build.log')
assert file

text = file.text
assert text.contains("Results: 5 specs, 4 failures")

return true