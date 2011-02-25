(function() {
	var jasmineMavenPlugin = window.jasmineMavenPlugin = window.jasmineMavenPlugin || {};
	jasmineMavenPlugin.printReport = function(reporter) {
		var result = buildReport(reporter,reporter.suites(),0);
		result += describeFailureSentences(reporter);
		return result;
	};
		
	var indent = function(indentLevel) {
		var indentStr = '';
		for(var i=0;i<indentLevel;i++) {
			indentStr += '  ';
		}
		return indentStr;
	};

	var describeMessages = function(messages,indentLevel) {
		var message = ' <<< FAILURE!';
		for(var i=0;i<messages.length;i++) {
			message += '\n'+indent(indentLevel)+'* '+messages[i].message;		
		}
		return message;
	};

	var inArray = function(arr,val) {
		var result = false;
		for(var i=0;i<arr.length;i++) {
			if(arr[i] === val) {
				result = true;
				break;
			}
		}
		return result;
	};

	var reportedItems = [];

	var buildReport = function(reporter,items,indentLevel) {
		var line = '';
	 	for(var i=0;i<items.length;i++){
			var item = items[i];	
			if(!inArray(reportedItems,item)) {
				line += (i > 0 && indentLevel === 0 ? '\n' : '')+"\n"+indent(indentLevel)+item.name;

				if(item.type == 'spec') {
					var result = reporter.results()[item.id];
					if(result && result.result == 'failed') {
						line += describeMessages(result.messages,indentLevel+1);
					}
				}

				reportedItems.push(item);
				line += buildReport(reporter,item.children,indentLevel+1);
			}
		}
		return line;
	};
	
	var buildFailureSentences = function(reporter,components,failures,sentence) {	
		for (var i=0; i < components.length; i++) {
			var component = components[i];
			var desc = sentence ? sentence + ' ' + component.name : component.name;
			var children = component.children;
			if(children && children.length > 0) {
				buildFailureSentences(reporter,children,failures,desc);
			} else { 
				var result = reporter.results()[component.id];
				if(result && result.result === 'failed') {
					failures.push(desc + describeMessages(result.messages,2));
				}
			}
		};
	};
	
	var describeFailureSentences = function(reporter) {
		var result = '';
		var failures = [];
		buildFailureSentences(reporter,reporter.suites(),failures);
		if(failures.length > 0) {
			result += '\n\n';
			result += failures.length + ' failure' + (failures.length !== 1 ? 's' : '') + ':'
			for (var i=0; i < failures.length; i++) {
				result += '\n\n  ' + (i+1) + '.) ' + failures[i];
			};
		}
		return result;
	};
})();

if(typeof window.reporter !== 'undefined') {
	jasmineMavenPlugin.printReport(window.reporter);
}