var specCount = 0; 
var failCount=0; 
for(var key in reporter.results()) { 
	specCount++; if(reporter.results()[key].result == 'failed') failCount++; 
} 
specCount+' specs, '+failCount+' failures';