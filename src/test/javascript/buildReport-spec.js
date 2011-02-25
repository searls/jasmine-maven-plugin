describe("Building the console report", function() {
  var reporter;
	beforeEach(function() {
	  reporter = this.reporterHelper();
	});
	
	describe("#printReport", function() {
		var output,lines;		
		
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
		  expect(lines[12]).toContain('<<< FAILURE!');
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
	
		it("indicates the number of failures", function() {
		  expect(lines[15]).toContain('1 failure:');
		});
		
		it("contains a full expansion of the failed spec as a sentence", function() {
		  expect(lines[17]).toContain('1.) Your Project Feature B B.1 B.1.a it dices just by looking at it the wrong way');
		});
		
		it("indicates a failure on the summary line", function() {
		  expect(lines[17]).toContain(' <<< FAILURE!');
		});
		
		it("contains the expectation text on the next line", function() {
		  expect(lines[18]).toContain("Expected 'Awesome idea' to contain 'Terrible'.");
		});
				
	});
	
	
});