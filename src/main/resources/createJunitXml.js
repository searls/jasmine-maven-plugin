var junitXmlReporter;

(function() {
	junitXmlReporter = {
		prolog: '<?xml version="1.0" encoding="UTF-8" ?>',
		report: function(reporter) {
			if (!reporter)
				throw 'Jasmine JS API Reporter must not be null.';
			if (reporter.finished !== true)
				throw 'Jasmine runner is not finished!';

			var results = this.crunchResults(reporter.results_);
			
			var writer = new XmlWriter();
			writer.beginNode('testsuite');
			writer.attrib('errors','0');
			writer.attrib('name','jasmine.specs');
			writer.attrib('tests',results.tests);
			writer.attrib('failures',results.failures);
			writer.attrib('skipped',results.skipped);
			writer.attrib('hostname','localhost');
			writer.attrib('time', '0.0');
			writer.attrib('timestamp',this.currentTimestamp());
			this.writeChildren(reporter, writer, reporter.suites_,'');
			writer.endNode();
			
			return this.prolog+writer.toString();
		},
		writeChildren: function(reporter, writer, tests,runningName) {
			for(var i=0;i<tests.length;i++) {
				var name = (runningName.length > 0 ? runningName+' ' : '')+tests[i].name;
				if(tests[i].type === 'spec') {
					var specResult = reporter.results_[tests[i].id] || {};
					this.writeTestcase(writer,specResult,name);
				}
				this.writeChildren(reporter, writer,tests[i].children,name);
			}
		},
		writeTestcase: function(writer,specResult,name) {
			var failure = specResult.result !== 'passed';
			writer.beginNode('testcase');
			writer.attrib('classname','jasmine');
			writer.attrib('name',name);
			writer.attrib('time','0.0');
			writer.attrib('failure',failure+'');
			if(failure) {
				this.writeError(writer,specResult);
			}
			writer.endNode();
		},
		writeError: function(writer,specResult) {
			writer.beginNode('error');
			var message = '';
			var type = '';
			var messages = specResult.messages || [];
			for(var j=0;j<messages.length;j++) {
				message += messages[j].message;
				type = messages[j].type + '.' + messages[j].matcherName;
			}
			writer.attrib('type',type);
			writer.attrib('message',message);
			writer.writeString(message);
			writer.endNode();
		},
		crunchResults: function(results) {
			var count=0;
			var fails=0;
			var last;
			for(var key in results) {
				count++;
				if(results[key].result === 'failed') {
					fails++;
				}
				last = key;
			}
			return {
				tests: count.toString(), 
				failures: fails.toString(),
				skipped: count > 0 ? (1+parseInt(last)-count).toString() : "0"
			};
		},
		currentTimestamp: function() {
			var f = function(n) {
		        // Format integers to have at least two digits.
		        return n < 10 ? '0' + n : n;
		    }

			var date = new Date();
			
	        return date.getUTCFullYear()   + '-' +
	             f(date.getUTCMonth() + 1) + '-' +
	             f(date.getUTCDate())      + 'T' +
	             f(date.getUTCHours())     + ':' +
	             f(date.getUTCMinutes())   + ':' +
	             f(date.getUTCSeconds());
		}
	};

	//From here: http://www.codeproject.com/KB/ajax/XMLWriter.aspx
	function XmlWriter() {
		this.XML = [];
		this.nodes = [];
		this.State = "";
		this.formatXml = function(Str) {
			if (Str)
				return Str.replace(/&/g, "&amp;").replace(/\"/g, "&quot;")
						.replace(/</g, "&lt;").replace(/>/g, "&gt;");
			return ""
		}
		this.beginNode = function(Name) {
			if (!Name)
				return;
			if (this.State == "beg")
				this.XML.push(">");
			this.State = "beg";
			this.nodes.push(Name);
			this.XML.push("<" + Name);
		}
		this.endNode = function() {
			if (this.State == "beg") {
				this.XML.push("/>");
				this.nodes.pop();
			} else if (this.nodes.length > 0)
				this.XML.push("</" + this.nodes.pop() + ">");
			this.State = "";
		}
		this.attrib = function(Name, Value) {
			if (this.State != "beg" || !Name)
				return;
			this.XML.push(" " + Name + "=\"" + this.formatXml(Value) + "\"");
		}
		this.writeString = function(Value) {
			if (this.State == "beg")
				this.XML.push(">");
			this.XML.push(this.formatXml(Value));
			this.State = "";
		}
		this.node = function(Name, Value) {
			if (!Name)
				return;
			if (this.State == "beg")
				this.XML.push(">");
			this.XML.push((Value == "" || !Value) ? "<" + Name + "/>" : "<"
					+ Name + ">" + this.formatXml(Value) + "</" + Name + ">");
			this.State = "";
		}
		this.close = function() {
			while (this.nodes.length > 0)
				this.endNode();
			this.State = "closed";
		}
		this.toString = function() {
			return this.XML.join("");
		}
	}

})();
