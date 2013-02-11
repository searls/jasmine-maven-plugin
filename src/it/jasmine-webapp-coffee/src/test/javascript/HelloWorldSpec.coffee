describe "HelloWorld", ->
  it "should say hello", ->
    hello_world = new HelloWorld
    expect(hello_world.greeting()).toBe "Hello, World"

  it "should say 今日は", ->
    hello_world = new HelloWorld
    expect(hello_world.japaneseGreeting()).toBe "今日は"
