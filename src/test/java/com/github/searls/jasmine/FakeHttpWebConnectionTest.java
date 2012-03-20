package com.github.searls.jasmine;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.mockito.Mockito;

import com.gargoylesoftware.htmlunit.WebConnection;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;

/**
 * Tests for {@link FakeHttpWebConnection}.
 * @author Richard Corfield &lt;m0rjc@m0rjc.me.uk&gt;
 */
public class FakeHttpWebConnectionTest {

	@Test
	public void testForExpectedClasspathEntries() throws Exception {
		MavenProject projectMock = Mockito.mock(MavenProject.class);
		
		List<String> testElements = Arrays.asList(new String[]{"test1file","test2file"});
		Mockito.when(projectMock.getTestClasspathElements()).thenReturn(testElements);
		
		Set<Artifact> artifacts = new HashSet<Artifact>();
		artifacts.add(mockArtifact(Artifact.SCOPE_TEST, new File("test.jar")));
		artifacts.add(mockArtifact(Artifact.SCOPE_COMPILE, new File("compile.jar")));
		artifacts.add(mockArtifact(Artifact.SCOPE_RUNTIME, new File("runtime.jar")));
		
		Mockito.when(projectMock.getArtifacts()).thenReturn(artifacts);
		
		FakeHttpWebConnection subject = new FakeHttpWebConnection(null, projectMock);
		
		URLClassLoader loader = (URLClassLoader) subject.classLoader;
		URL[] urls = loader.getURLs();
		Assert.assertEquals(5, urls.length);
		checkUrl(urls[0], "test1file");
		checkUrl(urls[1], "test2file");

		// Set order not guaranteed.
		int index = 2;
		for(Artifact artifact : artifacts){
			checkUrl(urls[index], artifact.getFile().getName());
			index++;
		}
	}
	
	@Test
	public void testDelegatesToWrappedForOtherRequests() throws Exception {
		MavenProject projectMock = Mockito.mock(MavenProject.class);
		
		List<String> testElements = Collections.emptyList();
		Mockito.when(projectMock.getTestClasspathElements()).thenReturn(testElements);
		
		Set<Artifact> artifacts = new HashSet<Artifact>();
		Mockito.when(projectMock.getArtifacts()).thenReturn(artifacts);
		
		WebConnection wrapped = Mockito.mock(WebConnection.class);
		Mockito.when(wrapped.getResponse(Mockito.any(WebRequest.class))).thenReturn(null);
		
		FakeHttpWebConnection subject = new FakeHttpWebConnection(wrapped, projectMock);
		WebRequest request = new WebRequest(new URL("http://news.bbc.co.uk/"));
		subject.getResponse(request);

		Mockito.verify(wrapped).getResponse(request);
	}
	
	@Test
	public void testWillLoadFromClaspath() throws Exception {
		MavenProject projectMock = Mockito.mock(MavenProject.class);
		
		List<String> testElements = new ArrayList<String>();
		testElements.add("src/test/resources"); // Expecting to run in the project root directory.
		Mockito.when(projectMock.getTestClasspathElements()).thenReturn(testElements);
		
		Set<Artifact> artifacts = new HashSet<Artifact>();
		Mockito.when(projectMock.getArtifacts()).thenReturn(artifacts);
		
		FakeHttpWebConnection subject = new FakeHttpWebConnection(null, projectMock);
		WebResponse response = subject.getResponse(new WebRequest(new URL("http://maven.test.dependencies/HelloWorld.js")));
		
		Assert.assertEquals(200, response.getStatusCode());
		String data = response.getContentAsString();
		Assert.assertTrue(data.startsWith("var HelloWorld = function() {"));
		Assert.assertEquals("application/javascript", response.getContentType());
	}

	@Test
	public void testWillReturnNotFoundForNotFound() throws Exception {
		MavenProject projectMock = Mockito.mock(MavenProject.class);
		
		List<String> testElements = new ArrayList<String>();
		testElements.add("src/test"); // Expecting to run in the project root directory.
		Mockito.when(projectMock.getTestClasspathElements()).thenReturn(testElements);
		
		Set<Artifact> artifacts = new HashSet<Artifact>();
		Mockito.when(projectMock.getArtifacts()).thenReturn(artifacts);
		
		FakeHttpWebConnection subject = new FakeHttpWebConnection(null, projectMock);
		WebResponse response = subject.getResponse(new WebRequest(new URL("http://maven.test.dependencies/resources/ThisFileDoesNotExist.js")));
		
		Assert.assertEquals(404, response.getStatusCode());
	}

	@Test
	public void testAllowsFakeHostNameToBeChanged() throws Exception {
		MavenProject projectMock = Mockito.mock(MavenProject.class);
		
		List<String> testElements = new ArrayList<String>();
		testElements.add("src/test/resources"); // Expecting to run in the project root directory.
		Mockito.when(projectMock.getTestClasspathElements()).thenReturn(testElements);
		
		Set<Artifact> artifacts = new HashSet<Artifact>();
		Mockito.when(projectMock.getArtifacts()).thenReturn(artifacts);
		
		FakeHttpWebConnection subject = new FakeHttpWebConnection(null, projectMock);
		subject.setFakeHost("an.alternate.host.com");
		WebResponse response = subject.getResponse(new WebRequest(new URL("http://an.alternate.host.com/HelloWorld.js")));
		
		Assert.assertEquals(200, response.getStatusCode());
		String data = response.getContentAsString();
		Assert.assertTrue(data.startsWith("var HelloWorld = function() {"));
	}
	
	/**
	 * Ext-JS appends ?_dc=12312225 to the end of its requests to cripple caching. 
	 * This can be turned off, but seems on by default.
	 */
	@Test
	public void testCorrectlyHandlesExtJsStyleCacheBreakingParameter() throws Exception {
		MavenProject projectMock = Mockito.mock(MavenProject.class);
		
		List<String> testElements = new ArrayList<String>();
		testElements.add("src/test/resources"); // Expecting to run in the project root directory.
		Mockito.when(projectMock.getTestClasspathElements()).thenReturn(testElements);
		
		Set<Artifact> artifacts = new HashSet<Artifact>();
		Mockito.when(projectMock.getArtifacts()).thenReturn(artifacts);
		
		FakeHttpWebConnection subject = new FakeHttpWebConnection(null, projectMock);
		WebResponse response = subject.getResponse(new WebRequest(new URL("http://maven.test.dependencies/HelloWorld.js?_dc=12233241234")));
		
		Assert.assertEquals(200, response.getStatusCode());
		String data = response.getContentAsString();
		Assert.assertTrue(data.startsWith("var HelloWorld = function() {"));
		Assert.assertEquals("application/javascript", response.getContentType());
	}
	
	
	/** 
	 * Check that the resultant url contains the expected portion.
	 * I can't make assumptions on the machine this test is running on, so can't use
	 * absolute URLs.
	 */
	private void checkUrl(URL url, String expectedText){
		Assert.assertTrue(expectedText + " not found in " + url,  url.getFile().contains(expectedText));
	}
	
	private Artifact mockArtifact(String scope, File file){
		Artifact a = Mockito.mock(Artifact.class);
		Mockito.when(a.getScope()).thenReturn(scope);
		Mockito.when(a.getFile()).thenReturn(file);
		return a;
	}
}
