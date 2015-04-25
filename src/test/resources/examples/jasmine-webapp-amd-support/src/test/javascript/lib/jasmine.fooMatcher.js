beforeEach(function() {
  jasmine.addMatchers({
    toEqualFoo : function(util, customEqualityTesters) {
      return {
        compare: function(actual, expected) {
          var rx = new RegExp("Foo" + expected);
          var passed = rx.test(actual);
          return {
            pass: passed,
            message: 'Expected ' + actual + (passed ? '' : ' not') + ' to match ' + expected
          };
        }
      };
    }
  });
});