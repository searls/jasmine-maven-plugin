//File depends on z.js

var zDescription = new Z().describe();
var A = function() {
  this.describe = function() {
    return 'Not '+zDescription;
  }
};