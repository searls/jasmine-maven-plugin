describe('HelloWorld',function(){
	
	it('should say hello',function(){
		var helloWorld = new HelloWorld();
		expect(helloWorld.greeting()).toBe("Hello, World");
	});
	
	for(var i=0; i<10;i++) {
		it('fails',function(){
			expect(true).toBe(false);
		});
	}
		
});