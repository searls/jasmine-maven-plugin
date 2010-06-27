package searls.jasmine.runner;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.junit.Test;

public class SpecRunnerHtmlGeneratorTest {

	private SpecRunnerHtmlGenerator specRunnerHtmlGenerator = new SpecRunnerHtmlGenerator();
	
	@Test
	public void shouldBuildBasicHtmlWhenNoDependenciesAreProvided() {
		List<Artifact> deps = new ArrayList<Artifact>();
		String html = specRunnerHtmlGenerator.generate(deps);
		assertThat(html,containsString("<html>"));
		assertThat(html,containsString("</html>"));
	}
	
	@Test
	public void shouldPopulateJasmineSourceIntoHtmlWhenProvided() throws Exception {
		String expectedContents = "javascript()";
		List<Artifact> deps = new ArrayList<Artifact>();
		deps.add(mockDependency("com.pivotallabs", "jasmine", "0.11.1", "js", expectedContents));
		
		String html = specRunnerHtmlGenerator.generate(deps);
		
		assertThat(html,containsString("<script type=\"text/javascript\">"+expectedContents+"</script>"));
	}
	
	@Test
	public void shouldPopulateMultipleJavascriptSourcesIntoHtmlWhenProvided() throws Exception {
		String jasmineString = "javascript_jasmine()";
		String jasmineHtmlString = "javascript_jasmine_html()";
		List<Artifact> deps = new ArrayList<Artifact>();
		deps.add(mockDependency("com.pivotallabs", "jasmine", "0.11.1", "js", jasmineString));
		deps.add(mockDependency("com.pivotallabs", "jasmine-html", "0.11.1", "js", jasmineHtmlString));		
		
		String html = specRunnerHtmlGenerator.generate(deps);
		
		assertThat(html,containsString("<script type=\"text/javascript\">"+jasmineString+"</script>"));
		assertThat(html,containsString("<script type=\"text/javascript\">"+jasmineHtmlString+"</script>"));
	}
	
	@Test
	public void shouldPopulateCSSIntoHtmlWhenProvided() throws Exception {
		String css = "h1 { background-color: awesome}";
		
		List<Artifact> deps = new ArrayList<Artifact>();
		deps.add(mockDependency("com.pivotallabs", "jasmine-css", "0.11.1", "css", css));
		
		String html = specRunnerHtmlGenerator.generate(deps);
		
		assertThat(html,containsString("<style type=\"text/css\">"+css+"</style>"));
	}
	
	private Artifact mockDependency(String groupId, String artifactId, String version, String type,String fileContents) throws Exception {
		Artifact dep = mock(Artifact.class);
		when(dep.getGroupId()).thenReturn(groupId);
		when(dep.getArtifactId()).thenReturn(artifactId);
		when(dep.getVersion()).thenReturn(version);
		when(dep.getType()).thenReturn(type);
		
		File f = File.createTempFile("test", ".js");
		FileUtils.writeStringToFile(f, fileContents);
		when(dep.getFile()).thenReturn(f);
		
		return dep;
	}
	
}
