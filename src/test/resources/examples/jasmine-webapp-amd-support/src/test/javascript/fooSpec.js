define(['foo', '/spec/lib/jasmine.fooMatcher.js'], function(foo, fooMatcher) {
  describe("Foo", function() {


    beforeEach(function(){
      jasmine.addMatchers(fooMatcher);
    });

    it("Prepends Foo", function() {
      expect(foo.foo("bar")).toEqualFoo("bar");
    });
  });
});