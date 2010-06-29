describe('FailSpec',function(){
	
	it('should fail',function() {
		expect(true).toBe(false);
	});
	
	describe('NestedFail',function() {
		it('should fail deeply',function() {
			expect(true).toBe(false);
		});
	})
	
});