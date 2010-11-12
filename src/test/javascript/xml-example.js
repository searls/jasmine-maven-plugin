describe('Your Project',function(){
	
	it('is named Slice-o-matic',function(){});
	
	describe('Feature A',function() {
		
		it('slices',function() {});
		
		it('does not slice *that*',function(){});
		
		xit('prevents cuts to your thumb',function(){});
	});
	
	describe('Feature B',function() {
		
		it('dices',function() {});
		
		describe('B.1',function() {
		
			it('dices finely',function() {});
			
			it('dices roughly',function() {});
			
			describe('B.1.a',function() {
				it('dices just by looking at it the wrong way',function(){
					expect('Awesome idea').toContain('Terrible');
				});
			});
			
		});
		
	});
});