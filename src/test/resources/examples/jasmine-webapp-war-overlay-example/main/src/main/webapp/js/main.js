// Require.js allows us to configure shortcut alias
require.config({
	// The shim config allows us to configure dependencies for
	// scripts that do not call define() to register a module
	shim: {
	},
	paths: {
	}
});

require([
    'a',
	'b'
], function( a, b ) {

	var resultArea = document.getElementById("resultTextArea");
	resultArea.value = b.getResultOfA();
});