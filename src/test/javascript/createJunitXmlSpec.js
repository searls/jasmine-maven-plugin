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
			
			it('has name jasmine.specs',function(){
				expect(find('testsuite')).toHaveAttr('name','jasmine.specs');
			});
			
			it('has localhost as hostname',function(){
				expect(find('testsuite')).toHaveAttr('hostname','localhost');
			})
			
			it('has 7 tests',function(){
				expect(find('testsuite')).toHaveAttr('tests','7');
			});
			
			it('has time 0.0 (because jsApiReporter tells us nothing of time',function(){
				expect(find('testsuite')).toHaveAttr('time','0.0');	
			});
			
			it('has a UTC timestamp',function() {
				var timestamp = find('testsuite').attr('timestamp');
				var date = new Date();
				expect(timestamp).toContain(date.getUTCFullYear());
				expect(timestamp).toContain(date.getUTCMonth());
				expect(timestamp).toContain(date.getUTCDate());
				expect(timestamp).toContain(date.getUTCHours());
				expect(timestamp).toContain(date.getUTCMinutes());
				expect(timestamp).toContain(date.getUTCSeconds());
			});
			
			describe('testcase elements',function() {
				it('has 7 elements',function() {
					expect(find('testsuite testcase').length).toBe(7);
				});
				
				describe('first test',function(){
					var testcase;
					
					beforeEach(function(){
						testcase = find('testsuite testcase:eq(0)');
					});
					 
					it('is named "Your Project is named Slice-o-matic"',function(){
						expect(testcase).toHaveAttr('name','Your Project is named Slice-o-matic');
					});
					
					it('has time 0.0 (because jsapireporter has nothing on it)',function(){
						expect(testcase).toHaveAttr('time','0.0');
					});
					
					it('was not a failure',function(){
						expect(testcase).toHaveAttr('failure','false');
					});
					
					it('has a classname of jasmine',function(){
						expect(testcase).toHaveAttr('classname','jasmine');
					});
					
					it('has no error child',function(){
						expect(testcase.find('error')).not.toExist();
					});
				});
				
				describe('third test',function(){
					var testcase;
					
					beforeEach(function(){
						testcase = find('testsuite testcase:eq(2)');
					});
					 
					it('is named "Your Project Feature A does not slice *that*"',function(){
						expect(testcase).toHaveAttr('name','Your Project Feature A does not slice *that*');
					});
				});

				describe('seventh (failing) test',function(){
					var testcase;
					
					beforeEach(function(){
						testcase = find('testsuite testcase:eq(6)');
					});
					 
					it('was a failure',function(){
						expect(testcase).toHaveAttr('failure','true');
					});
					
					describe('its error element',function() {
						var error;
						beforeEach(function(){
							error = testcase.find('error');
						});
						
						it('exists',function(){
							expect(error).toExist();
						});
						
						it('has type "expect.toContain"',function(){
							expect(error).toHaveAttr('type','expect.toContain');
						});
						
						it('has message "Expected \'Awesome idea\' to contain \'Terrible\'."',function(){
							expect(error).toHaveAttr('message',"Expected 'Awesome idea' to contain 'Terrible'.");
						});
						
						it('has text "Expected \'Awesome idea\' to contain \'Terrible\'."',function(){
							expect(error.text()).toBe("Expected 'Awesome idea' to contain 'Terrible'.");
						});
					});
				});
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