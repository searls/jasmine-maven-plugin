define(['requireJasmine'], function(j) {

	describe("Require.js and jasmine", function() {
		it("says hello", function () {
			expect(j.helloWorld()).toEqual("Hei!");
		});
	});
});
