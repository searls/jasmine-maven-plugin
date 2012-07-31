describe('A',function(){

  it('should not be Z',function(){
    var a = new A();
    expect(a.describe()).toBe("Not the letter Z!");
  });

});