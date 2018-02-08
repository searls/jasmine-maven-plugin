describe('Browser',function(){
  it('has Firefox/45.0 in its user agent string',function() {
    expect(navigator.userAgent).toContain("Firefox/45.0");
  })
});
