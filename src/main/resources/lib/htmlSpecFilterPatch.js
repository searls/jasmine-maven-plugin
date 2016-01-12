(function() {
  // Patched version of HtmlSpecFilter that works with HtmlUnit
  jasmineRequire.HtmlSpecFilter = function() {
    function HtmlSpecFilter(options) {
      var filterString = options && options.filterString() && options.filterString().replace(/[-[\]{}()*+?.,\\^$|#\s]/g, "\\$&");
      var filterPattern = new RegExp(filterString || "");

      this.matches = function(specName) {
        return filterPattern.test(specName);
      };
    }

    return HtmlSpecFilter;
  };

})();