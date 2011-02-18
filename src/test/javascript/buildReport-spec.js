describe("Building reports", function() {
  var reporter;
	beforeEach(function() {
	  reporter = this.reporterHelper();
	});
	
	describe("#buildReport", function() {
		var output;		
		var line = function(i) { return output[i]; };
		
		describe("documentation format", function() {
		  beforeEach(function() {
		    output = buildReport(reporter,reporter.suites(),0).split('\n');
		  });
		  
			it("has line 1: Your Project", function() {
			  expect(line(1)).toBe('Your Project');
			});

			it("does not indent line 1", function() {
			  expect(line(1)).toHaveIndent(0);
			});
			
			it("has line 2: is named Slice-o-matic", function() {
			  expect(line(2)).toBeTrimmed('is named Slice-o-matic');
			});
			
			it("indents line 2 once", function() {
			  expect(line(2)).toHaveIndent(1);
			});
		
			it("prints: Feature A", function() {
			  expect(line(3)).toBeTrimmed('Feature A');
			});
			
			it("indents line 3 once", function() {
			  expect(line(3)).toHaveIndent(1);
			});
			
			it("prints: slices", function() {
			  expect(line(4)).toBeTrimmed('slices');
			});
			
			it("indents line 4 twice", function() {
			  expect(line(4)).toHaveIndent(2);
			});
			
		});
		
	});
	
	
});