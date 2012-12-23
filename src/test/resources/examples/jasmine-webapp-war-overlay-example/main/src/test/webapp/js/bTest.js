define([
       'b'
], function(b) {
	
	describe('Tests for module b', function() {
		
		it('returns the result of a', function() {
			var result = b.getResultOfA();
			
			expect(result).toEqual(2);
		});
		
	});
	
});