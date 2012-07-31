describe('IE6',function(){
  it('has MSIE in its user agent string',function() {
    expect(navigator.userAgent).toContain("MSIE");
  })
});