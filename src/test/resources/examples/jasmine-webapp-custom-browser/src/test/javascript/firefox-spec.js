describe('Browser',function(){
  it('has Firefox in its user agent string',function() {
    expect(navigator.userAgent).toContain("Firefox");
  })
});
