define(["jquery", "kinetic"], function($, kinetic) {
    describe("phantomJS driver support", function() {
        it("can attach and unattach kinetic objects", function() {
            function createStage(container) {
                return new kinetic.Stage({
                    container: container,
                    width: 100,
                    height: 100
                });
            }

            function createShape() {
                return new kinetic.Rect({
                    x: 25,
                    y: 25,
                    width: 50,
                    height: 50,
                    fill: "black"
                });
            }

            function createLayer(container) {
                var stage = createStage(container);
                var layer = new kinetic.Layer();
                stage.add(layer);
                return layer;
            }

            var $container = $('<div></div>');
            $container.appendTo("body");
            var layer = createLayer($container[0]);
            var rectangle = createShape();
            layer.add(rectangle);
            var spyListener = jasmine.createSpy();
            rectangle.on("click", spyListener);
            rectangle.simulate("click");
            $container.remove();

            expect(spyListener).toHaveBeenCalled();
        });
    });
});
