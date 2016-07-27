describe('Default reporters and also reporter that stores custom information',function(){

  it('should retrieve value stored to window during test',function(){
    window.exampleTest = 'Hello World';
  });

  it('should say hello with normal test', function() {
    var helloWorld = new HelloWorld();
    expect(helloWorld.greeting()).toBe("Hello, World");
  });
});
