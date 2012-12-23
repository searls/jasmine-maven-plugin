define([
       'a'
], function(a) {
	function getResultOfA() {
		return a.getResult();
	}
	
	return {
		getResultOfA: getResultOfA
	};
});