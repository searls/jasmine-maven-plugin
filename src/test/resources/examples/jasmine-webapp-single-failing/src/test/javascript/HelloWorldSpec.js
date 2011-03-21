describe('HelloWorld',function(){
	
	it('should say hello',function(){
		var helloWorld = new HelloWorld();
		expect(helloWorld.greeting()).toBe("Hello, World");
	});
	
	it('wins',function() {});
	it('wins',function() {});
	it('wins',function() {});
	it('loses',function() {
		expect('sad').toBe('panda');
	});
	
});