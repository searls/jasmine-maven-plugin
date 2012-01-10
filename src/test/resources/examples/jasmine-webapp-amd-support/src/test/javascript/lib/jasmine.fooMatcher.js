jasmine.Matchers.prototype.toEqualFoo =  function(someting) {
  var rx = new RegExp("Foo" + someting);
	return rx.test(this.actual);
};
