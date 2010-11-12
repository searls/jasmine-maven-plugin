describe('Junit XML', function() {

	var reporter;
	var sut;

	beforeEach(function() {
		sut = junitXmlReporter;
		reporter = {
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
													} ]
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

		this.addMatchers( {
			toStartWith: function(expected) {
				return this.actual.indexOf(expected) === 0;
			}, 
			toThrowWithArgs: function(expected) {
				// shamefully cut-and-pasted from existing
				// toThrow():
				// https://github.com/pivotal/jasmine/raw/master/src/Matchers.js
				var result = false;
				var exception;
				if (typeof this.actual != 'function') {
					throw new Error('Actual is not a function');
				}
				try {
					// Remove the first arg of this invocation and
					// apply it to the provided function.
					var args = [];
					for(var i=1;i<arguments.length;i++) { args.push(arguments[i]); }
					this.actual.apply(this,args);
				} catch (e) {
					exception = e;
				}
				if (exception) {
					result = (expected === jasmine.undefined || this.env.equals_(exception.message || exception, expected.message || expected));
				}
				var not = this.isNot ? "not " : "";
				this.message = function() {
					if (exception && (expected === jasmine.undefined || !this.env.equals_(exception.message || exception, expected.message || expected))) {
						return ["Expected function " + not + "to throw", expected ? expected.message || expected : " an exception", ", but it threw", exception.message || exception].join(' ');
					} else {
						return "Expected function to throw an exception.";
					}
				};
				return result;				
			}
		});

	});

	describe('Basics', function() {
		it('is named junitXmlReporter', function() {
			expect(junitXmlReporter).toBeDefined();
		});
		it('throws up when reporter is null', function() {
			expect(junitXmlReporter.report).toThrowWithArgs('Jasmine JS API Reporter must not be null.',null);
		});
		it('throws an exception if invoked before ready', function() {
			reporter.finished = false;
			expect(junitXmlReporter.report).toThrowWithArgs('Jasmine runner is not finished!',reporter);
		});
	});

	describe('XML',function() {
		var result;
		
		var find = function(query) {
			return $('<x>'+result+'</x>').find(query);
		};
		
		beforeEach(function(){
			result = sut.report(reporter);
		});
		
		it('starts with a prolog',function(){
			expect(result).toStartWith('<?xml version="1.0" encoding="UTF-8" ?>');
		})
		
		describe('when test is unchanged',function(){
			it('has testsuite',function() {
				expect(find('testsuite')).toExist();
			});
			
			it('testsuite has zero errors (because all errors are failures in jasmine)',function(){
				expect(find('testsuite')).toHaveAttr('errors','0');
			});
			
			it('has one failure',function(){
				expect(find('testsuite')).toHaveAttr('failures','1');
			})
			
			it('has one skipped',function(){
				expect(find('testsuite')).toHaveAttr('skipped','1');
			});
			
			xit('skip',function(){});
			
			it('has name jasmine.specs',function(){
				expect(find('testsuite')).toHaveAttr('name','jasmine.specs');
			});
			
			it('has 7 tests',function(){
				expect(find('testsuite')).toHaveAttr('tests','7');
			});
			
		});
		
		describe('when results have been blanked out',function(){
			beforeEach(function(){
				reporter.results_ = {}
				result = sut.report(reporter);
			});
			
			it('has zero skipped when it has zero tests',function() {
				
				expect(find('testsuite')).toHaveAttr('skipped','0');
			});
		});
		
	});
	
});