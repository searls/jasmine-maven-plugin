file = new File(basedir, 'build.log')
assert file

text = file.text
assert text.contains('Results: 11 specs, 10 failures')

return true