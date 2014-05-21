describe('app',function(){

  it('should calculate to 50',function(){
    expect(app.calculate()).toBe(50);
  });

  it('should say "Hello, Jim"',function() {
    expect(app.sayHello('Jim'), 'Hello, Jim');
  });
});
