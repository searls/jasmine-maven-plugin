describe("my fixture spec", function() {
	beforeEach(function() {
		  loadFixtures('my-first-fixture.html');
	})
	
	it("sees the fixture", function() {
		expect($('#my-fixture')).toExist();
	});

});