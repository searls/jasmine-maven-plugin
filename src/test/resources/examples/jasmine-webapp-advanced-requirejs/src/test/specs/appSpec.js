define(['specs/helperSpec', 'app'], function($, app) {
	describe("App", function() {

		beforeEach(function() {
			setFixtures('<input id="simpleInput" type="text">');
		})

		it("adds datapicker-class to element", function () {
			var element = $('#simpleInput');
			app.addDatePickerToElement(element);
			expect(element).toHaveClass('datepicker');


		});

	});
});
