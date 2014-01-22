describe('Third party lib',function(){

  it('should be available for direct dependencies',function(){
      var bootStrapButton = typeof $("body").button;
      expect(bootStrapButton).toBe("function");
  });

  it('should be available for transitive dependencies',function(){
      var jQueryFromTransitive = $;
      expect(jQueryFromTransitive).toBeDefined();
  });

  it('should be available for <scope>test</scope>',function(){
      expect(AsyncSpec).toBeDefined();
  });

  it('should be available for <scope>runtime</scope>',function(){
      expect(_).toBeDefined();
  });

  it('should be available for war dependencies',function(){
      expect(foo).toBeDefined();
  });

  it('should be available for queries of WebJarAssetLocator',function(){
      expect(i18n).toBeDefined();
  });
});
