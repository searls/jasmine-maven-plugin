(function() {
  var jasmineMavenPlugin = window.jasmineMavenPlugin = window.jasmineMavenPlugin || {};
  var reporter,reportedItems,specCount,failureCount;

  jasmineMavenPlugin.printReport = function(r, config) {
    config = config || {};
    reporter = r, reportedItems=[], specCount=0, failureCount=0;
    var result;
    if (config.format === 'progress') {
      result = printProgressFormat(reporter);
    } else {
      result = buildDocumentationFormatReport(jasmine.getEnv().topSuite().children,0);
    }
    result += describeFailureSentences(reporter);
    result += "\n\nResults: "+specCount+" specs, "+failureCount+" failures\n";
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
    if(messages) {
      for(var i=0;i<messages.length;i++) {
        message += '\n'+indent(indentLevel)+'* '+messages[i].message;
      }
    } else {
      message += ' (Result is missing! Perhaps this spec did not execute?)';
    }
    return message;
  };

  var printProgressFormat = function(reporter) {
    var linesPerRow = 80;
    var result = '\n';
    report = buildProgressFormatReport(reporter.suites());
    if(report.length > linesPerRow) {
      for (var i=0; i < report.length; i+=linesPerRow) {
        result += report.substring(i,i+linesPerRow) + '\n';
      };
    } else {
      result += report;
    }
    return result;
  };

  var buildProgressFormatReport = function(items) {
    var output = '';
    for (var i=0; i < items.length; i++) {
      var item = items[i];
      if(item.type == 'spec') {
        specCount++;
        var result = resultForSpec(item);
        if(result.result !== 'passed') {
          failureCount++;
          output += 'F';
        } else {
          output += '.';
        }
      }
      reportedItems.push(item);
      output += buildProgressFormatReport(item.children);
    }
    return output;
  };

  var buildDocumentationFormatReport = function(items,indentLevel) {
    var line = '';
    if (items) {
      for(var i=0;i<items.length;i++){
        var item = items[i];
        if(!inArray(reportedItems,item)) {
          line += (i > 0 && indentLevel === 0 ? '\n' : '')+"\n"+indent(indentLevel)+item.description;

          if(item instanceof jasmine.Spec) {
            specCount++;
            var result = resultForSpec(item);
            if(result.status !== 'passed') {
              failureCount++;
              line += describeMessages(result.failedExpectations,indentLevel+1);
            }
          }

          reportedItems.push(item);
          line += buildDocumentationFormatReport(item.children,indentLevel+1);
        }
      }
    }
    return line;
  };

  var buildFailureSentences = function(components,failures,sentence) {
    for (var i=0; i < components.length; i++) {
      var component = components[i];
      var desc = sentence ? sentence + ' ' : '';
      var children = component.children;
      if(children && children.length > 0) {
        buildFailureSentences(children,failures,desc+component.name);
      } else {
        var result = resultForSpec(component);
        if(result.result !== 'passed') {
          failures.push(desc + 'it ' + component.name + describeMessages(result.messages,2));
        }
      }
    }
  };

  var resultForSpec = function(spec){
    var specResults = reporter.specs();
    for (var i=0; i < specResults.length; i++) {
      if (spec.id == specResults[i].id) {
        return specResults[i];
      }
    }
    return {};
  };

  var describeFailureSentences = function() {
    var result = '';
    var failures = [];
    buildFailureSentences(reporter.suites(),failures);
    if(failures.length > 0) {
      result += '\n\n';
      result += failures.length + ' failure' + (failures.length !== 1 ? 's' : '') + ':'
      for (var i=0; i < failures.length; i++) {
        result += '\n\n  ' + (i+1) + '.) ' + failures[i];
      };
    }
    return result;
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

})();