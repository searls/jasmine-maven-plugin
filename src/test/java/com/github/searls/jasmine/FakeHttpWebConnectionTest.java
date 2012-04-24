package com.github.searls.jasmine;

import java.net.URL;
import java.util.ArrayList;
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
		WebResponse response = subject.getResponse(new WebRequest(new URL("http://localhost:8234/HelloWorld.js")));
		
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
		WebResponse response = subject.getResponse(new WebRequest(new URL("http://localhost:8234/resources/ThisFileDoesNotExist.js")));
		
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
		WebResponse response = subject.getResponse(new WebRequest(new URL("http://localhost:8234/HelloWorld.js?_dc=12233241234")));
		
		Assert.assertEquals(200, response.getStatusCode());
		String data = response.getContentAsString();
		Assert.assertTrue(data.startsWith("var HelloWorld = function() {"));
		Assert.assertEquals("application/javascript", response.getContentType());
	}
}
