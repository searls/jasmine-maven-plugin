var junitXmlReporter;

(function() {
  junitXmlReporter = {
    prolog: '<?xml version="1.0" encoding="UTF-8" ?>',
    report: function(reporter,debug) {
      if (!reporter)
        throw 'Jasmine JS API Reporter must not be null.';
      if (reporter.status() !== 'done' && !debug)
        throw 'Jasmine runner is not finished!';

      var results = this.crunchResults(reporter.specs());

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
      this.writeSpecResults(reporter, writer, reporter.specs());
      writer.endNode();

      return this.prolog+writer.toString();
    },
    writeSpecResults: function(reporter, writer, specResults) {
      for(var i=0;i<specResults.length;i++) {
        this.writeTestcase(writer,specResults[i]);
      }
    },
    writeTestcase: function(writer,specResult) {
      var failure = specResult.status !== 'passed';
      writer.beginNode('testcase');
      writer.attrib('classname','jasmine');
      writer.attrib('name',specResult.fullName);
      writer.attrib('time','0.0');
      writer.attrib('failure',failure+'');
      if(failure) {
        this.writeFailure(writer,specResult);
      }
      writer.endNode();
    },
    writeFailure: function(writer,specResult) {      
      var messages = specResult.failedExpectations || [];
      for(var j=0;j<messages.length;j++) {
        writer.beginNode('failure');
        writer.attrib('type', messages[j].matcherName);
        writer.attrib('message', messages[j].message);
        writer.writeString(messages[j].message + '\n');
        // TODO : maybe better =>  "expected:<" + messages[j].expected + "> but was:<" + messages[j].actual + ">" + "\n" +
        // TODO : include Error.stack => stack only supported in Mozilla browsers, but it seems HtmlUnit doesn't include a supporting browserVersion
        writer.endNode();
      }
    },
    crunchResults: function(results) {
      var count=0;
      var fails=0;
      var last;
      for(var key in results) {
        count++;
        if(results[key].status !== 'passed') {
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
