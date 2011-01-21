/** Console X
  * http://github.com/deadlyicon/consolex.js
  *
  * By Jared Grippe <jared@jaredgrippe.com>
  *
  * Copyright (c) 2009 Jared Grippe
  * Licensed under the MIT license.
  *
  * consolex avoids ever having to see javascript bugs in browsers that do not implement the entire
  * firebug console suit
  *
  */
(function(window) {
  window.console || (window.console = {});

  var names = ["log", "debug", "info", "warn", "error", "assert", "dir", "dirxml",
  "group", "groupEnd", "time", "timeEnd", "count", "trace", "profile", "profileEnd"];

  function emptyFunction(){}

  for (var i = 0; i < names.length; ++i){
    console[names[i]] || (console[names[i]] = emptyFunction);
    if (typeof console[names[i]] !== 'function')
      console[names[i]] = (function(method) {
          return function(){ return Function.prototype.apply.apply(method, [console,arguments]); };
        })(console[names[i]]);
  }
  
  // V8 and Rihno Support
  if (typeof print === "function"){
    if (console.log === emptyFunction) console.log = print;
    
    function print_with_prefix(prefix, args){
      Array.prototype.unshift.call(args, prefix);
      print.apply(this, args);
    }
    
    if (console.info === emptyFunction) console.info = function(){
      print_with_prefix('INFO:', arguments);
    }
    if (console.warn === emptyFunction) console.warn = function(){
      print_with_prefix('WARN:', arguments);
    }
    if (console.error === emptyFunction) console.error = function(){
      print_with_prefix('ERROR:', arguments);
    }
  }
  
})(this);