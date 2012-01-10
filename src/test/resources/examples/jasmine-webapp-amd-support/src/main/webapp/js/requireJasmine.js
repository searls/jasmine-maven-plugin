define(['lib/dep'], function(dep) {
	return {
		helloWorld: function() {
			return dep.hello();
		}
	}
});
