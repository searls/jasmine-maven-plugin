package com.github.searls.jasmine.maven;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Stephen Fry
 *
 */
public class MavenResourceProviderTest {

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
		
		MavenResourceProvider subject = new MavenResourceProvider(projectMock);
		
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
