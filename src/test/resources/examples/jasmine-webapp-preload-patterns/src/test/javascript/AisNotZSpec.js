describe('A',function(){

  it('should not be Z',function(){
    var a = new A();
    if (TestLib.doTest()) {
    	expect(a.describe()).toBe("Not the letter Z");
    }
  });

});