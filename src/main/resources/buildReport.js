var indent = function(indentLevel) {
	var indent = '';
	for(var i=0;i<indentLevel;i++) {
		indent += '  ';
	}
	return indent;
}

var buildMessages = function(messages,indentLevel) {
	var message = '';
	for(var i=0;i<messages.length;i++) {
		message += '\n'+indent(indentLevel)+' * '+messages[i].message;		
	}
	return message;
}

var buildReport = function(items,indentLevel) {
	var line = '';
 	for(var i=0;i<items.length;i++){
		var item = items[i];
		line += "\n"+indent(indentLevel)+(item.type == 'suite' ? 'describe ' : 'it ')+item.name;
		
		if(item.type == 'spec') {
			var result = reporter.results()[item.id];
			if(result.result == 'failed') {
				line += ' <<< FAILURE!';
				line += buildMessages(result.messages,indentLevel+1);
			}
		}
		line += ' '+buildReport(item.children,indentLevel+1);
	}
	return line;
}

buildReport(reporter.suites(),0);