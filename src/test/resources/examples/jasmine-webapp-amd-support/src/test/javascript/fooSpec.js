require(['foo'], function(foo) {
	describe("Foo", function() {
		it("Prepends Foo", function() {
			expect(foo.foo("bar")).toEqualFoo("bar");
		});
	});
});