describe("Building the console report", function() {
  var reporter;
  beforeEach(function() {
    reporter = this.reporterHelper();
  });

  describe("#printReport", function() {
    var output,lines;
    var lineFromBottom = function(i) { return lines[lines.length-i]; };
    var behavesLikeItPrintsTheSummary = function() {
      it("indicates the number of failures", function() {
        expect(lineFromBottom(9)).toContain('2 failures:');
      });

      it("contains a full expansion of the failed spec as a sentence", function() {
        expect(lineFromBottom(7)).toContain('1.) Your Project Feature B B.1 B.1.a it dices just by looking at it the wrong way');
      });

      it("indicates a failure on the summary line", function() {
        expect(lineFromBottom(7)).toContainFailure();
      });

      it("contains the expectation text on the next line", function() {
        expect(lineFromBottom(6)).toContain("Expected 'Awesome idea' to contain 'Terrible'.");
      });

      it("prints failure for the spec missing a message", function() {
        expect(lineFromBottom(4)).toContainFailure();
      });

      it("prints suggestion that the spec result is missing or did not run", function() {
        expect(lineFromBottom(4)).toContain('Result is missing! Perhaps this spec did not execute?');
      });

      it("prints the final result on the last line", function() {
        expect(lineFromBottom(2)).toContain("Results: 8 specs, 2 failures");
      });
    };

    describe("documentation format", function() {
      beforeEach(function() {
        output = jasmineMavenPlugin.printReport(reporter);
        lines = output.split('\n');
      });

      xit("prints itself", function() {
        console.log(output)
      });

      it("has line 1: Your Project", function() {
        expect(lines[1]).toBe('Your Project');
      });

      it("has no indent", function() {
        expect(lines[1]).toHaveIndent(0);
      });

      it("has line 2: is named Slice-o-matic", function() {
        expect(lines[2]).toBeTrimmed('is named Slice-o-matic');
      });

      it("indents line 2 once", function() {
        expect(lines[2]).toHaveIndent(1);
      });

      it("prints: Feature A", function() {
        expect(lines[3]).toBeTrimmed('Feature A');
      });

      it("indents line 3 once", function() {
        expect(lines[3]).toHaveIndent(1);
      });

      it("prints: slices", function() {
        expect(lines[4]).toBeTrimmed('slices');
      });

      it("indents line 4 twice", function() {
        expect(lines[4]).toHaveIndent(2);
      });

      it("has a failure on line 12", function() {
        expect(lines[12]).toContainFailure();
      });

      it("indented line 12 four times", function() {
        expect(lines[12]).toHaveIndent(4);
      });

      it("contains the expectation text on the next line", function() {
        expect(lines[13]).toContain("Expected 'Awesome idea' to contain 'Terrible'.");
      });

      it("indents the expectation text once more than the line above", function() {
        expect(lines[13]).toHaveIndent(5);
      });

      behavesLikeItPrintsTheSummary();
    });
    describe("progress format", function() {
      beforeEach(function() {
        output = jasmineMavenPlugin.printReport(reporter,{
          format: 'progress'
        });
        lines = output.split('\n');
      });

      xit("prints itself", function() {
        console.log(output)
      });

      it("contains one line", function() {
        expect(lines[1]).toContain('......FF');
      });

      behavesLikeItPrintsTheSummary();
    });
    describe("progress format with a ton of specs", function() {
      beforeEach(function() {
        addManySpecs(240+35);
        output = jasmineMavenPlugin.printReport(reporter,{
          format: 'progress'
        });
        lines = output.split('\n');
      });

      xit("prints itself", function() {
        console.log(output)
      });

      it("starts line 1 with 2 dots", function() {
        expect(lines[1].substring(0,6)).toBe(manyDots(6));
      });

      it("continues line 1 with 2 Fs", function() {
        expect(lines[1].substring(6,8)).toBe('FF');
      });

      it("ends line 1 with 72 dots", function() {
        expect(lines[1].substring(8,80)).toBe(manyDots(72));
      });

      it("has 80 dots on line 2", function() {
        expect(lines[2]).toContain(manyDots(80));
      });

      it("has 80 dots on line 3", function() {
        expect(lines[3]).toContain(manyDots(80));
      });

      it("has 43 dots on line 4", function() {
        expect(lines[4]).toContain(manyDots(43));
      });

      var manyDots = function(num) {
        var s = '';
        for (var i=0; i < num; i++) {
          s+='.'
        };
        return s;
      };

      var addManySpecs = function(num) {
        for (var i=0; i < num; i++) {
          var id = i+30;
          reporter.suites_[0].children.push({
            "id" : id,
            "name" : "is padding",
            "type" : "spec",
            "children" : []
          });
          reporter.results_[id] = {
            "messages" : [],
            "result" : "passed"
          };
        };
      };
    });
  });


});