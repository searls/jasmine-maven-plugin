(function(window) {
  window.app = {
    calculate : function() {
      return math.add(15,35);
    },
    sayHello : function(name) {
      return util.append("Hello, ", name);
    }
  };
})(this);
