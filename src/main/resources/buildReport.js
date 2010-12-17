var indent = function(indentLevel) {
	var indentStr = '';
	for(var i=0;i<indentLevel;i++) {
		indentStr += '  ';
	}
	return indentStr;
}

var buildMessages = function(messages,indentLevel) {
	var message = '';
	for(var i=0;i<messages.length;i++) {
		message += '\n'+indent(indentLevel)+' * '+messages[i].message;		
	}
	return message;
}

var reportedItems = [];

var buildReport = function(items,indentLevel) {
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
	var line = '';
 	for(var i=0;i<items.length;i++){
		var item = items[i];	
		if(!inArray(reportedItems,item)) {
			line += "\n"+indent(indentLevel)+(item.type == 'suite' ? 'describe ' : 'it ')+item.name;
			
			if(item.type == 'spec') {
				var result = reporter.results()[item.id];
				if(result && result.result == 'failed') {
					line += ' <<< FAILURE!';
					line += buildMessages(result.messages,indentLevel+1);
				}
			}
			
			reportedItems.push(item);
			line += ' '+buildReport(item.children,indentLevel+1);
		}
	}
	return line;
}

buildReport(reporter.suites(),0);