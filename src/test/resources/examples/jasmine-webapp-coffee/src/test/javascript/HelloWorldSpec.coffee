describe "HelloWorld", ->
  it "should say hello", ->
    hello_world = new HelloWorld
    expect(hello_world.greeting()).toBe "Hello, World"