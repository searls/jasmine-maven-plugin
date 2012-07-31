beforeEach(function() {
  this.addMatchers({
    toHaveIndent: function(indentLevel) {
      var actualIndent = this.actual.split(/[\S]/)[0].length / 2.0;
      this.message = function() { return "Expected indent level "+indentLevel+" but was "+actualIndent+" on string '"+this.actual+"'"; };

      return actualIndent === indentLevel;
    },
    toBeTrimmed: function(expected) {
      return $.trim(this.actual) === expected;
    },
    toContainFailure: function() {
      return this.actual.indexOf('<<< FAILURE!') !== -1;
    }
  });
   this.reporterHelper = function(){
    var r = {
      "started" : true,
      "finished" : true,
      "suites_" : [ {
        "id" : 0,
        "name" : "Your Project",
        "type" : "suite",
        "children" : [
            {
              "id" : 0,
              "name" : "is named Slice-o-matic",
              "type" : "spec",
              "children" : []
            },
            {
              "id" : 1,
              "name" : "Feature A",
              "type" : "suite",
              "children" : [ {
                "id" : 1,
                "name" : "slices",
                "type" : "spec",
                "children" : []
              }, {
                "id" : 2,
                "name" : "does not slice *that*",
                "type" : "spec",
                "children" : []
              } ]
            },
            {
              "id" : 2,
              "name" : "Feature B",
              "type" : "suite",
              "children" : [
                  {
                    "id" : 4,
                    "name" : "dices",
                    "type" : "spec",
                    "children" : []
                  },
                  {
                    "id" : 3,
                    "name" : "B.1",
                    "type" : "suite",
                    "children" : [
                        {
                          "id" : 5,
                          "name" : "dices finely",
                          "type" : "spec",
                          "children" : []
                        },
                        {
                          "id" : 6,
                          "name" : "dices roughly",
                          "type" : "spec",
                          "children" : []
                        },
                        {
                          "id" : 4,
                          "name" : "B.1.a",
                          "type" : "suite",
                          "children" : [ {
                            "id" : 7,
                            "name" : "dices just by looking at it the wrong way",
                            "type" : "spec",
                            "children" : []
                          },
                          {
                            "id" : 8,
                            "name" : "I'm a spec that didn't quite execute and has a missing result",
                            "type" : "spec",
                            "children" : []
                          }
                           ]
                        } ]
                  } ]
            } ]
      } ],
      "results_" : {
        "0" : {
          "messages" : [],
          "result" : "passed"
        },
        "1" : {
          "messages" : [],
          "result" : "passed"
        },
        "2" : {
          "messages" : [],
          "result" : "passed"
        },
        "4" : {
          "messages" : [],
          "result" : "passed"
        },
        "5" : {
          "messages" : [],
          "result" : "passed"
        },
        "6" : {
          "messages" : [],
          "result" : "passed"
        },
        "7" : {
          "messages" : [ {
            "type" : "expect",
            "matcherName" : "toContain",
            "passed_" : false,
            "expected" : "Terrible",
            "actual" : "Awesome idea",
            "message" : "Expected 'Awesome idea' to contain 'Terrible'.",
            "trace" : {
              "message" : "Expected 'Awesome idea' to contain 'Terrible'."
            }
          } ],
          "result" : "failed"
        }
      }
    };
    r.suites = function() { return r.suites_; };
    r.results = function() { return r.results_; };
    return r;
  };
});