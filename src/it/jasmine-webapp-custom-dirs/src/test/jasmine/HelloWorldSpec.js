describe('HelloWorld',function(){

  it('should say hello',function(){
    var helloWorld = new HelloWorld();
    expect(helloWorld.greeting()).toBe("Hello, World");
  });

});