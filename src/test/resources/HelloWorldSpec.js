describe('HelloWorld',function(){

  it('should say hello',function(){
    var helloWorld = new HelloWorld();
    expect(helloWorld.greeting()).toBe("Hello, World");
  });

  //Intentional fail:
  it('should say goodbye',function(){
    var helloWorld = new HelloWorld();
    expect(helloWorld.greeting()).toBe("Goodbye, World");
  });

  it('should fail',function() {
    expect(5).toBe(6);
  });

});